package com.practicum.playlistmaker.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.ItunesService
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.Track
import com.practicum.playlistmaker.TrackAdapter
import com.practicum.playlistmaker.player.PlayerActivity
import com.practicum.playlistmaker.search.data.SearchHistory
import com.practicum.playlistmaker.search.data.SearchRepository
import com.practicum.playlistmaker.search.presentation.SearchPresenter
import com.practicum.playlistmaker.search.presentation.SearchTracksView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity(), SearchTracksView {

    private var searchText: String = ""
    private var lastUnsuccessfulSearch: String = ""
    private var mainThreadHandler: Handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var trackList = ArrayList<Track>()

    @SuppressLint("NotifyDataSetChanged")
    val searchResultAdapter = TrackAdapter {
        if (clickDebounce()) {
            if (searchHistory.addTrack(it)) {
                searchHistoryAdapter.notifyDataSetChanged()
            } else searchHistoryAdapter.notifyItemInserted(0)
            val playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra(Track::class.java.simpleName, it)
            startActivity(playerIntent)
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT).toString()
        }

        initializeLateinitItems()
        presenter = SearchPresenter(
            this,
            searchHistory,
            SearchRepository(retrofit.create(ItunesService::class.java))
        )

        val toolbar = findViewById<Toolbar>(R.id.search_toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        searchEditText.setText(searchText)
        searchEditText.addTextChangedListener(searchTextWatcher)

        clearImage.setOnClickListener {
            presenter.clearSearchTextPressed()
        }

        findViewById<RecyclerView?>(R.id.search_history_recycler_view).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = searchHistoryAdapter
        }

        refreshSearchButton.setOnClickListener {
            searchText = lastUnsuccessfulSearch
            searchTracks()
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            presenter.searchEditTextFocusChanged(hasFocus, searchText)
        }

        clearSearchHistoryButton.setOnClickListener {
            presenter.clearSearchHistoryPressed()
            searchHistoryAdapter.notifyDataSetChanged()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onStop() {
        super.onStop()
        searchHistory.updateSharedPref()
    }

    override fun clearSearchText() {
        searchText = ""
        searchEditText.setText(searchText)
        clearImage.visibility = View.GONE
    }

    override fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    override fun hideSearchResult() {
        clearTracks(searchResultAdapter)
    }

    override fun showSearchResult(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        searchRecyclerView.visibility = View.VISIBLE
        trackList.clear()
        trackList.addAll(tracks)
        searchResultAdapter.trackList = trackList
        searchResultAdapter.notifyDataSetChanged()
        showMessage(SearchState.SUCCESSFUL_SEARCH)
    }

    override fun showEmptySearch() {
        progressBar.visibility = View.GONE
        searchRecyclerView.visibility = View.VISIBLE
        clearTracks(searchResultAdapter)
        showMessage(SearchState.NOTHING_IS_FOUND)
    }

    override fun showSearchError() {
        progressBar.visibility = View.GONE
        clearTracks(searchResultAdapter)
        showMessage(SearchState.UNSUCCESSFUL_CONNECTION)
    }

    override fun setSearchResultVisible() {
        searchLayout.visibility = View.VISIBLE
        searchHistoryLayout.visibility = View.GONE
        clearSearchHistoryButton.visibility = View.GONE
    }

    override fun setSearchHistoryVisible() {
        searchLayout.visibility = View.GONE
        searchHistoryLayout.visibility = View.VISIBLE
        clearSearchHistoryButton.visibility = View.VISIBLE
    }

    override fun searchDebounce() {
        mainThreadHandler.removeCallbacks(searchRunnable)
        mainThreadHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    override fun saveSearchRequest(searchRequest: String) {
        searchText = searchRequest
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
                val playerIntent = Intent(this, PlayerActivity::class.java)
                playerIntent.putExtra(Track::class.java.simpleName, it)
                startActivity(playerIntent)
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

    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            presenter.searchEditTextFocusChanged(searchEditText.hasFocus(), p0?.toString())
        }

        override fun afterTextChanged(editable: Editable?) {
            if (editable?.isNotEmpty() == true) clearImage.visibility = View.VISIBLE
            else clearImage.visibility = View.GONE
            searchText = searchEditText.text.toString()
        }
    }

    private val searchRunnable = Runnable {
        searchTracks()
    }

    private fun searchTracks() {
        searchRecyclerView.visibility = View.GONE
        showMessage(SearchState.SUCCESSFUL_SEARCH)
        progressBar.visibility = View.VISIBLE
        presenter.loadTracks(searchText)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearTracks(adapter: TrackAdapter) {
        adapter.trackList.clear()
        adapter.notifyDataSetChanged()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainThreadHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun showMessage(searchState: SearchState) {
        when (searchState) {
            SearchState.UNSUCCESSFUL_CONNECTION -> {
                setViewsVisibility(
                    textVisibility = true,
                    imageVisibility = true,
                    buttonVisibility = true
                )
                setViewsResources(
                    R.string.no_internet_connection,
                    R.drawable.no_internet_connection
                )
                lastUnsuccessfulSearch = searchText
            }
            SearchState.NOTHING_IS_FOUND -> {
                setViewsVisibility(
                    textVisibility = true,
                    imageVisibility = true,
                    buttonVisibility = false
                )
                setViewsResources(R.string.nothing_is_found, R.drawable.nothing_is_found)
            }
            SearchState.SUCCESSFUL_SEARCH -> {
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
        const val BASE_URL = "https://itunes.apple.com"
        const val SHARED_PREFERENCE = "SHARED_PREFERENCE"
        const val SEARCH_DEBOUNCE_DELAY = 2_000L
        const val CLICK_DEBOUNCE_DELAY = 1_000L
    }
}

enum class SearchState {
    UNSUCCESSFUL_CONNECTION,
    NOTHING_IS_FOUND,
    SUCCESSFUL_SEARCH
}
