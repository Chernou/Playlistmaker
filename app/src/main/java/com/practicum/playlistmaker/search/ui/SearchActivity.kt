package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.view_model.SearchViewModel
import com.practicum.playlistmaker.search.view_model.SearchRouter
import com.practicum.playlistmaker.search.view_model.SearchState
import com.practicum.playlistmaker.search.view_model.api.SearchTracksView
import com.practicum.playlistmaker.utils.Creator

class SearchActivity : AppCompatActivity(), SearchTracksView {

    private var lastUnsuccessfulSearch: String = ""
    private var mainThreadHandler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private var trackList = ArrayList<Track>()

    @SuppressLint("NotifyDataSetChanged")
    val searchResultAdapter = TrackAdapter {
        if (clickDebounce()) {
            presenter.onTrackPressed(it) //todo notifyItemInserted when appropriate
        }
    }

    private lateinit var presenter: SearchViewModel
    private lateinit var clearImage: ImageView
    private lateinit var searchHistoryAdapter: TrackAdapter
    private lateinit var searchEditText: EditText
    private lateinit var searchErrorImageView: ImageView
    private lateinit var searchErrorTextView: TextView
    private lateinit var refreshSearchButton: Button
    private lateinit var searchHistoryTextView: TextView
    private lateinit var clearSearchHistoryButton: Button
    private lateinit var searchLayout: ViewGroup
    private lateinit var searchHistoryLayout: ViewGroup
    private lateinit var progressBar: ProgressBar
    private lateinit var searchRecyclerView: RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initializeLateinitItems()
        presenter = Creator.provideSearchPresenter(this, SearchRouter(this), this)
        presenter.onCreate()
        if (savedInstanceState != null) {
            searchEditText.text = savedInstanceState.getCharSequence(SEARCH_TEXT) as Editable
        }

        findViewById<RecyclerView?>(R.id.search_history_recycler_view).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = searchHistoryAdapter
        }

        val toolbar = findViewById<Toolbar>(R.id.search_toolbar)
        toolbar.setNavigationOnClickListener {
            presenter.onBackArrowPressed()
        }

        searchEditText.addTextChangedListener(searchTextWatcher)

        clearImage.setOnClickListener {
            presenter.onClearSearchTextPressed()
        }

        refreshSearchButton.setOnClickListener {
            presenter.onRefreshSearchButtonPressed(lastUnsuccessfulSearch)
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            presenter.searchEditTextFocusChanged(hasFocus, searchEditText.text.toString())
        }

        clearSearchHistoryButton.setOnClickListener {
            presenter.onClearSearchHistoryPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(SEARCH_TEXT, searchEditText.text)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleared()
    }

    override fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showProgressBar()
            is SearchState.SearchContent -> showSearchResult(state.tracks)
            is SearchState.HistoryContent -> showSearchHistoryLayout(state.tracks)
            is SearchState.EmptySearch -> showEmptySearch()
            is SearchState.Error -> showSearchError()
            is SearchState.EmptyScreen -> showEmptyScreen()
        }
    }

    override fun clearSearchText() {
        searchEditText.text.clear()
        clearImage.visibility = View.GONE
    }

    override fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    override fun clearSearchResult() {
        searchResultAdapter.trackList.clear()
        searchResultAdapter.notifyDataSetChanged()
    }

    override fun refreshSearchHistoryAdapter() {
        searchHistoryAdapter.notifyDataSetChanged()
    }

    override fun showSearchResultLayout() {
        searchLayout.visibility = View.VISIBLE
        searchHistoryLayout.visibility = View.GONE
        clearSearchHistoryButton.visibility = View.GONE
        showMessage(MessageType.NO_MESSAGE)
    }

    private fun showEmptyScreen() {
        progressBar.visibility = View.GONE
        searchLayout.visibility = View.GONE
        searchHistoryLayout.visibility = View.GONE
    }

    private fun showSearchResult(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        searchRecyclerView.visibility = View.VISIBLE
        trackList.clear()
        trackList.addAll(tracks)
        searchResultAdapter.trackList = trackList
        searchResultAdapter.notifyDataSetChanged()
        showMessage(MessageType.NO_MESSAGE)
    }

    private fun showSearchHistoryLayout(searchHistory: List<Track>) {
        searchHistoryAdapter.trackList = searchHistory as ArrayList<Track>
        searchHistoryAdapter.notifyDataSetChanged()
        searchLayout.visibility = View.GONE
        searchHistoryLayout.visibility = View.VISIBLE
        clearSearchHistoryButton.visibility = View.VISIBLE
        showMessage(MessageType.NO_MESSAGE)
    }

    private fun showEmptySearch() {
        progressBar.visibility = View.GONE
        searchRecyclerView.visibility = View.VISIBLE
        searchResultAdapter.trackList.clear()
        searchResultAdapter.notifyDataSetChanged()
        showMessage(MessageType.NOTHING_IS_FOUND)
    }

    private fun showSearchError() {
        progressBar.visibility = View.GONE
        searchResultAdapter.trackList.clear()
        searchResultAdapter.notifyDataSetChanged()
        showMessage(MessageType.UNSUCCESSFUL_CONNECTION)
    }

    private fun showProgressBar() {
        searchRecyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        showMessage(MessageType.NO_MESSAGE)
    }

    private fun initializeLateinitItems() {
        searchErrorTextView = findViewById(R.id.search_result_text)
        searchErrorImageView = findViewById(R.id.search_result_image)
        refreshSearchButton = findViewById(R.id.refresh_search_button)
        searchHistoryTextView = findViewById(R.id.search_history_text_view)
        clearSearchHistoryButton = findViewById(R.id.clear_search_history_button)
        searchLayout = findViewById(R.id.search_result_layout)
        searchHistoryLayout = findViewById(R.id.search_history_layout)
        searchHistoryAdapter = TrackAdapter {
            if (clickDebounce()) {
                presenter.onTrackPressed(it)
            }
        }
        searchEditText = findViewById(R.id.search_edit_text)
        clearImage = findViewById(R.id.clear_image)
        progressBar = findViewById(R.id.progress_bar)
        searchRecyclerView = findViewById<RecyclerView?>(R.id.search_recycler_view).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = searchResultAdapter
        }
    }

    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            presenter.searchEditTextFocusChanged(searchEditText.hasFocus(), p0.toString() ?: "")
            //todo hide search result when editText is empty
        }

        override fun afterTextChanged(editable: Editable?) {
            if (editable?.isNotEmpty() == true) clearImage.visibility = View.VISIBLE
            else clearImage.visibility = View.GONE
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

    private fun showMessage(messageType: MessageType) {
        when (messageType) {
            MessageType.UNSUCCESSFUL_CONNECTION -> {
                setViewsVisibility(
                    textVisibility = true,
                    imageVisibility = true,
                    buttonVisibility = true
                )
                setViewsResources(
                    R.string.no_internet_connection,
                    R.drawable.no_internet_connection
                )
                lastUnsuccessfulSearch = searchEditText.text.toString()
            }
            MessageType.NOTHING_IS_FOUND -> {
                setViewsVisibility(
                    textVisibility = true,
                    imageVisibility = true,
                    buttonVisibility = false
                )
                setViewsResources(R.string.nothing_is_found, R.drawable.nothing_is_found)
            }
            MessageType.NO_MESSAGE -> {
                setViewsVisibility(
                    textVisibility = false,
                    imageVisibility = false,
                    buttonVisibility = false
                )
            }
        }
    }

    private fun setViewsVisibility(
        textVisibility: Boolean,
        imageVisibility: Boolean,
        buttonVisibility: Boolean
    ) {
        searchErrorTextView.visibility = if (textVisibility) View.VISIBLE else View.GONE
        searchErrorImageView.visibility = if (imageVisibility) View.VISIBLE else View.GONE
        refreshSearchButton.visibility = if (buttonVisibility) View.VISIBLE else View.GONE
    }

    private fun setViewsResources(text: Int, image: Int) {
        searchErrorTextView.text = getString(text)
        searchErrorImageView.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                image
            )
        )
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val CLICK_DEBOUNCE_DELAY = 1_000L
    }
}

enum class MessageType {
    UNSUCCESSFUL_CONNECTION,
    NOTHING_IS_FOUND,
    NO_MESSAGE
}
