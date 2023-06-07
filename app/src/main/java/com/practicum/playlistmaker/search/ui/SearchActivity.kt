package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.view_model.ClearTextState
import com.practicum.playlistmaker.search.view_model.SearchState
import com.practicum.playlistmaker.search.view_model.SearchViewModel
import com.practicum.playlistmaker.utils.NavigationRouter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SearchActivity : AppCompatActivity() {

    private val mainThreadHandler: Handler by inject()
    private var isClickAllowed = true
    private var trackList = ArrayList<Track>()
    private val viewModel: SearchViewModel by viewModel()
    private val router: NavigationRouter by inject {
        parametersOf(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    val searchResultAdapter = TrackAdapter {
        if (clickDebounce()) {
            viewModel.onTrackPressed(it) //todo notifyItemInserted when appropriate
            router.openTrack(OPEN_TRACK_INTENT, it)
        }
    }

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchHistoryAdapter: TrackAdapter
    private lateinit var searchRecyclerView: RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeLateinitItems()

        viewModel.observeState().observe(this) {
            render(it)
        }
        if (savedInstanceState != null) {
            binding.searchEditText.text = savedInstanceState.getCharSequence(SEARCH_TEXT) as Editable
        }

        findViewById<RecyclerView?>(R.id.search_history_recycler_view).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = searchHistoryAdapter
        }

        val toolbar = findViewById<Toolbar>(R.id.search_toolbar)
        toolbar.setNavigationOnClickListener {
            router.goBack()
        }

        binding.searchEditText.addTextChangedListener(searchTextWatcher)

        viewModel.observeClearTextState().observe(this) { clearTextState ->
            if (clearTextState is ClearTextState.ClearText) {
                clearSearchText()
                hideKeyboard()
                viewModel.textCleared()
            }
        }

        binding.clearTextImage.setOnClickListener {
            viewModel.onClearTextPressed()
        }

        binding.refreshSearchButton.setOnClickListener {
            viewModel.onRefreshSearchButtonPressed()
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onFocusChanged(hasFocus, binding.searchEditText.text.toString())
        }

        binding.clearSearchHistoryButton.setOnClickListener {
            viewModel.onClearSearchHistoryPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(SEARCH_TEXT, binding.searchEditText.text)
    }

    override fun onDestroy() {
        super.onDestroy()
        router.onDestroy()
        viewModel.onCleared()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
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
        binding.searchEditText.text.clear()
        binding.clearTextImage.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            viewModel.onTextChanged(p0.toString() ?: "")
            //todo hide search result when editText is empty
        }

        override fun afterTextChanged(editable: Editable?) {
            if (editable?.isNotEmpty() == true) {
                binding.clearTextImage.visibility = View.VISIBLE
            } else {
                binding.clearTextImage.visibility = View.GONE
            }
        }
    }

    private fun showEmptyScreen() {
        searchRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.searchErrorLayout.visibility = View.GONE
        binding.searchHistoryLayout.visibility = View.GONE
    }

    private fun showSearchResult(tracks: List<Track>) {
        searchRecyclerView.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.searchErrorLayout.visibility = View.GONE
        binding.searchHistoryLayout.visibility = View.GONE
        trackList.clear()
        trackList.addAll(tracks)
        searchResultAdapter.trackList = trackList
        searchResultAdapter.notifyDataSetChanged()
    }

    private fun showSearchHistoryLayout(searchHistory: List<Track>) {
        searchRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.searchErrorLayout.visibility = View.GONE
        binding.searchHistoryLayout.visibility = View.VISIBLE
        searchHistoryAdapter.trackList = searchHistory as ArrayList<Track>
        searchHistoryAdapter.notifyDataSetChanged()
    }

    private fun showEmptySearch(emptySearchMessage: String) {
        searchRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.searchErrorLayout.visibility = View.VISIBLE
        binding.searchHistoryLayout.visibility = View.GONE
        searchResultAdapter.trackList.clear()
        searchResultAdapter.notifyDataSetChanged()
        binding.searchErrorText.text = emptySearchMessage
        binding.searchErrorImage.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.nothing_is_found
            )
        )
    }

    private fun showSearchError(errorMessage: String) {
        searchRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.searchErrorLayout.visibility = View.VISIBLE
        binding.searchHistoryLayout.visibility = View.GONE
        searchResultAdapter.trackList.clear()
        searchResultAdapter.notifyDataSetChanged()
        binding.searchErrorText.text = errorMessage
        binding.searchErrorImage.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.no_internet_connection
            )
        )
    }

    private fun showProgressBar() {
        searchRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.searchErrorLayout.visibility = View.GONE
        binding.searchHistoryLayout.visibility = View.GONE
    }

    private fun initializeLateinitItems() {
        searchHistoryAdapter = TrackAdapter {
            if (clickDebounce()) {
                viewModel.onTrackPressed(it)
                router.openTrack(OPEN_TRACK_INTENT, it)
            }
        }
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
        const val OPEN_TRACK_INTENT = "TRACK INTENT"
    }
}
