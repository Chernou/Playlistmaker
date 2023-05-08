package com.practicum.playlistmaker.search.presentation

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
import com.practicum.playlistmaker.TrackAdapter
import com.practicum.playlistmaker.search.data.SearchHistory
import com.practicum.playlistmaker.search.presentation.api.SearchTracksView
import com.practicum.playlistmaker.utils.Creator

class SearchActivity : AppCompatActivity(), SearchTracksView {

    private var lastUnsuccessfulSearch: String = ""
    private var mainThreadHandler: Handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private var trackList = ArrayList<Track>()

    @SuppressLint("NotifyDataSetChanged")
    val searchResultAdapter = TrackAdapter {
        if (clickDebounce()) {
            presenter.onTrackPressed(it) //todo notifyItemInserted when appropriate
        }
    }

    private lateinit var presenter: SearchPresenter
    private lateinit var clearImage: ImageView
    private lateinit var searchHistory: SearchHistory
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
        if (searchHistory.searchHistoryTrackList.isNotEmpty()) showSearchHistoryLayout()
        if (savedInstanceState != null) {
            searchEditText.text = savedInstanceState.getCharSequence(SEARCH_TEXT) as Editable
        }

        findViewById<RecyclerView?>(R.id.search_history_recycler_view).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = searchHistoryAdapter
        }

        presenter = Creator.provideSearchPresenter(this, searchHistory, SearchRouter(this))

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

    override fun onStop() {
        super.onStop()
        searchHistory.updateSharedPref()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler.removeCallbacksAndMessages(null)
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

    @SuppressLint("NotifyDataSetChanged")
    override fun clearSearchResult() {
        searchResultAdapter.trackList.clear()
        searchResultAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun showSearchResult(tracks: List<Track>) {
        mainThreadHandler.post {
            progressBar.visibility = View.GONE
            searchRecyclerView.visibility = View.VISIBLE
            trackList.clear()
            trackList.addAll(tracks)
            searchResultAdapter.trackList = trackList
            searchResultAdapter.notifyDataSetChanged()
            showMessage(MessageType.NO_MESSAGE)
        }
    }

    override fun showSearchResultLayout() {
        searchLayout.visibility = View.VISIBLE
        searchHistoryLayout.visibility = View.GONE
        clearSearchHistoryButton.visibility = View.GONE
    }

    override fun showSearchHistoryLayout() {
        searchLayout.visibility = View.GONE
        searchHistoryLayout.visibility = View.VISIBLE
        clearSearchHistoryButton.visibility = View.VISIBLE
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun showEmptySearch() {
        progressBar.visibility = View.GONE
        searchRecyclerView.visibility = View.VISIBLE
        searchResultAdapter.trackList.clear()
        searchResultAdapter.notifyDataSetChanged()
        showMessage(MessageType.NOTHING_IS_FOUND)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun showSearchError() {
        progressBar.visibility = View.GONE
        searchResultAdapter.trackList.clear()
        searchResultAdapter.notifyDataSetChanged()
        showMessage(MessageType.UNSUCCESSFUL_CONNECTION)
    }

    override fun showProgressBar() {
        searchRecyclerView.visibility = View.GONE
        showMessage(MessageType.NO_MESSAGE)
        progressBar.visibility = View.VISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun executeSearch() {
        searchDebounce() //todo remove from activity?
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun refreshSearchHistoryAdapter() {
        searchHistoryAdapter.notifyDataSetChanged()
    }

    private fun initializeLateinitItems() {
        searchErrorTextView = findViewById(R.id.search_result_text)
        searchErrorImageView = findViewById(R.id.search_result_image)
        refreshSearchButton = findViewById(R.id.refresh_search_button)
        searchHistoryTextView = findViewById(R.id.search_history_text_view)
        clearSearchHistoryButton = findViewById(R.id.clear_search_history_button)
        searchLayout = findViewById(R.id.search_frame_layout)
        searchHistoryLayout = findViewById(R.id.search_history_linear_layout)
        searchHistory = SearchHistory(getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE))
        searchHistoryAdapter = TrackAdapter {
            if (clickDebounce()) {
                presenter.onTrackPressed(it)
            }
        }
        searchHistoryAdapter.trackList = searchHistory.searchHistoryTrackList
        searchEditText = findViewById(R.id.search_edit_text)
        clearImage = findViewById(R.id.clear_image)
        progressBar = findViewById(R.id.progress_bar)
        searchRecyclerView = findViewById<RecyclerView?>(R.id.search_recycler_view).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = searchResultAdapter
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun searchDebounce() {
        mainThreadHandler.removeCallbacks(searchRunnable)
        mainThreadHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            presenter.searchEditTextFocusChanged(searchEditText.hasFocus(), p0?.toString())
        }

        override fun afterTextChanged(editable: Editable?) {
            if (editable?.isNotEmpty() == true) clearImage.visibility = View.VISIBLE
            else clearImage.visibility = View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val searchRunnable = Runnable {
        presenter.loadTracks(searchEditText.text.toString())
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
        const val SHARED_PREFERENCE = "SHARED_PREFERENCE"
        const val SEARCH_DEBOUNCE_DELAY = 2_000L
        const val CLICK_DEBOUNCE_DELAY = 1_000L
    }
}

enum class MessageType {
    UNSUCCESSFUL_CONNECTION,
    NOTHING_IS_FOUND,
    NO_MESSAGE
}
