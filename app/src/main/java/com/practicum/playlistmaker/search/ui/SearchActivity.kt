package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.view_model.ClearTextState
import com.practicum.playlistmaker.search.view_model.SearchViewModel
import com.practicum.playlistmaker.search.view_model.SearchState
import com.practicum.playlistmaker.utils.Creator

class SearchActivity : ComponentActivity() {

    private var lastUnsuccessfulSearch: String = ""
    private var mainThreadHandler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private var trackList = ArrayList<Track>()
    private val router = Creator.provideNavigationRouter(this)

    @SuppressLint("NotifyDataSetChanged")
    val searchResultAdapter = TrackAdapter {
        if (clickDebounce()) {
            viewModel.onTrackPressed(it) //todo notifyItemInserted when appropriate
            val playerIntent = Intent(this, PlayerActivity::class.java) //todo move intent to router?
            playerIntent.putExtra(Track::class.java.simpleName, it)
            startActivity(playerIntent)
        }
    }

    private lateinit var viewModel: SearchViewModel
    private lateinit var clearSearchTextImage: ImageView
    private lateinit var searchHistoryAdapter: TrackAdapter
    private lateinit var searchEditText: EditText
    private lateinit var searchErrorImageView: ImageView
    private lateinit var searchErrorTextView: TextView
    private lateinit var refreshSearchButton: Button
    private lateinit var searchHistoryTextView: TextView
    private lateinit var clearSearchHistoryButton: Button
    private lateinit var searchErrorLayout: ViewGroup
    private lateinit var searchHistoryLayout: ViewGroup
    private lateinit var progressBar: ProgressBar
    private lateinit var searchRecyclerView: RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initializeLateinitItems()
        viewModel = ViewModelProvider(this, SearchViewModel.getViewModelFactory())[SearchViewModel::class.java]

        viewModel.observeState().observe(this) {
            render(it)
        }
        viewModel.onCreate()
        if (savedInstanceState != null) {
            searchEditText.text = savedInstanceState.getCharSequence(SEARCH_TEXT) as Editable
        }

        findViewById<RecyclerView?>(R.id.search_history_recycler_view).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = searchHistoryAdapter
        }

        val toolbar = findViewById<Toolbar>(R.id.search_toolbar)
        toolbar.setNavigationOnClickListener {
            router.goBack()
        }

        searchEditText.addTextChangedListener(searchTextWatcher)

        viewModel.observeClearTextState().observe(this) {clearTextState ->
            if (clearTextState is ClearTextState.ClearText) {
                clearSearchText()
                hideKeyboard()
                viewModel.textCleared()
            }
        }

        clearSearchTextImage.setOnClickListener {
            viewModel.onClearTextPressed()
        }

        refreshSearchButton.setOnClickListener {
            viewModel.onRefreshSearchButtonPressed(lastUnsuccessfulSearch)
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.searchEditTextFocusChanged(hasFocus, searchEditText.text.toString())
        }

        clearSearchHistoryButton.setOnClickListener {
            viewModel.onClearSearchHistoryPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(SEARCH_TEXT, searchEditText.text)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onCleared()
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showProgressBar()
            is SearchState.SearchContent -> showSearchResult(state.tracks)
            is SearchState.HistoryContent -> showSearchHistoryLayout(state.tracks)
            is SearchState.EmptySearch -> showEmptySearch(state.emptySearchMessage)
            is SearchState.Error -> showSearchError(state.errorMessage)
            is SearchState.EmptyScreen -> showEmptyScreen()
        }
    }

    private fun clearSearchText() {
        searchEditText.text.clear()
        clearSearchTextImage.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            viewModel.searchEditTextFocusChanged(searchEditText.hasFocus(), p0.toString() ?: "")
            //todo hide search result when editText is empty
        }

        override fun afterTextChanged(editable: Editable?) {
            if (editable?.isNotEmpty() == true) {
                clearSearchTextImage.visibility = View.VISIBLE
            } else {
                clearSearchTextImage.visibility = View.GONE
            }
        }
    }

    private fun showEmptyScreen() {
        searchRecyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE
        searchErrorLayout.visibility = View.GONE
        searchHistoryLayout.visibility = View.GONE
    }

    private fun showSearchResult(tracks: List<Track>) {
        searchRecyclerView.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        searchErrorLayout.visibility = View.GONE
        searchHistoryLayout.visibility = View.GONE
        trackList.clear()
        trackList.addAll(tracks)
        searchResultAdapter.trackList = trackList
        searchResultAdapter.notifyDataSetChanged()
    }

    private fun showSearchHistoryLayout(searchHistory: List<Track>) {
        searchRecyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE
        searchErrorLayout.visibility = View.GONE
        searchHistoryLayout.visibility = View.VISIBLE
        searchHistoryAdapter.trackList = searchHistory as ArrayList<Track>
        searchHistoryAdapter.notifyDataSetChanged()
    }

    private fun showEmptySearch(emptySearchMessage: String) {
        searchRecyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE
        searchErrorLayout.visibility = View.VISIBLE
        searchHistoryLayout.visibility = View.GONE
        searchResultAdapter.trackList.clear()
        searchResultAdapter.notifyDataSetChanged()
        searchErrorTextView.text = emptySearchMessage
        searchErrorImageView.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.nothing_is_found
            )
        )
    }

    private fun showSearchError(errorMessage: String) {
        searchRecyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE
        searchErrorLayout.visibility = View.VISIBLE
        searchHistoryLayout.visibility = View.GONE
        searchResultAdapter.trackList.clear()
        searchResultAdapter.notifyDataSetChanged()
        searchErrorTextView.text = errorMessage
        searchErrorImageView.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.no_internet_connection
            )
        )
        lastUnsuccessfulSearch = searchEditText.text.toString()
    }

    private fun showProgressBar() {
        searchRecyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        searchErrorLayout.visibility = View.GONE
        searchHistoryLayout.visibility = View.GONE
    }

    private fun initializeLateinitItems() {
        searchErrorTextView = findViewById(R.id.search_result_text)
        searchErrorImageView = findViewById(R.id.search_result_image)
        refreshSearchButton = findViewById(R.id.refresh_search_button)
        searchHistoryTextView = findViewById(R.id.search_history_text_view)
        clearSearchHistoryButton = findViewById(R.id.clear_search_history_button)
        searchErrorLayout = findViewById(R.id.search_error_layout)
        searchHistoryLayout = findViewById(R.id.search_history_layout)
        searchHistoryAdapter = TrackAdapter {
            if (clickDebounce()) {
                viewModel.onTrackPressed(it)
                val playerIntent = Intent(this, PlayerActivity::class.java) //todo move intent to router?
                playerIntent.putExtra(Track::class.java.simpleName, it)
                startActivity(playerIntent)
            }
        }
        searchEditText = findViewById(R.id.search_edit_text)
        clearSearchTextImage = findViewById(R.id.clear_image)
        progressBar = findViewById(R.id.progress_bar)
        searchRecyclerView = findViewById<RecyclerView?>(R.id.search_recycler_view).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = searchResultAdapter
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainThreadHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val CLICK_DEBOUNCE_DELAY = 1_000L
    }
}
