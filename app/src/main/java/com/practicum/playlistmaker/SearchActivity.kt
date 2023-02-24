package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var searchText: String = ""
    private var lastUnsuccessfulSearch: String = ""
    private lateinit var clearImage: ImageView

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesService::class.java)

    var trackList = ArrayList<Track>()
    val trackAdapter = TrackAdapter {
        if (searchHistory.addTrack(it)) {
            searchHistoryAdapter.notifyDataSetChanged()
        } else searchHistoryAdapter.notifyItemInserted(0)
    }

    private lateinit var searchHistory: SearchHistory
    private lateinit var searchHistoryAdapter: TrackAdapter
    private lateinit var searchEditText: EditText
    private lateinit var searchErrorImageView: ImageView
    private lateinit var searchErrorTextView: TextView
    private lateinit var refreshSearchButton: Button
    private lateinit var searchHistoryTextView: TextView
    private lateinit var clearSearchButton: Button
    private lateinit var searchLayout: ViewGroup
    private lateinit var searchHistoryLayout: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT).toString()
        }

        initializeLateinitItems()

        val toolbar = findViewById<Toolbar>(R.id.search_toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        searchEditText.setText(searchText)
        searchEditText.addTextChangedListener(searchTextWatcher)

        clearImage.setOnClickListener {
            searchText = ""
            searchEditText.setText(searchText)
            clearTracks(trackAdapter)
            clearImage.visibility = View.GONE
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        findViewById<RecyclerView?>(R.id.search_recycler_view).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = trackAdapter
        }

        findViewById<RecyclerView?>(R.id.search_history_recycler_view).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = searchHistoryAdapter
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks(searchText)
                setSearchVisible()
                true
            }
            false
        }

        refreshSearchButton.setOnClickListener {
            searchTracks(lastUnsuccessfulSearch)
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchText.isEmpty() && searchHistory.searchHistoryTrackList.isNotEmpty()) {
                setSearchHistoryVisible()
            } else {
                setSearchVisible()
            }
        }

        clearSearchButton.setOnClickListener {
            searchHistory.clearSearchHistory()
            searchHistoryAdapter.notifyDataSetChanged()
            setSearchVisible()
        }
    }

    private fun initializeLateinitItems() {
        searchErrorTextView = findViewById(R.id.search_result_text)
        searchErrorImageView = findViewById(R.id.search_result_image)
        refreshSearchButton = findViewById(R.id.refresh_search_button)
        searchHistoryTextView = findViewById(R.id.search_history_text_view)
        clearSearchButton = findViewById(R.id.clear_search_history_button)
        searchLayout = findViewById(R.id.search_frame_layout)
        searchHistoryLayout = findViewById(R.id.search_history_linear_layout)
        searchHistory = SearchHistory(getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE))
        searchHistoryAdapter = TrackAdapter {}
        searchHistoryAdapter.trackList = searchHistory.searchHistoryTrackList
        searchEditText = findViewById(R.id.search_edit_text)
        clearImage = findViewById(R.id.clear_image)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onStop() {
        super.onStop()
        searchHistory.updateSharedPref()
    }

    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (searchEditText.hasFocus() && p0?.isEmpty() == true && searchHistory.searchHistoryTrackList.isNotEmpty()) {
                setSearchHistoryVisible()
            } else {
                setSearchVisible()
            }
        }

        override fun afterTextChanged(editable: Editable?) {
            if (editable?.isNotEmpty() == true) clearImage.visibility = View.VISIBLE
            else clearImage.visibility = View.GONE
            val searchEditText = findViewById<EditText>(R.id.search_edit_text)
            searchText = searchEditText.text.toString()
        }
    }

    private fun searchTracks(searchText: String) {

        itunesService.search(searchText).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.trackList?.isNotEmpty() == true) {
                        trackList.clear()
                        trackList.addAll(response.body()?.trackList!!)
                        trackAdapter.trackList = trackList
                        trackAdapter.notifyDataSetChanged()
                        showMessage(SearchState.SUCCESSFUL_SEARCH)
                        Log.d("!@#", response.code().toString())
                        Log.d("!@#", trackList.size.toString())
                    } else {
                        clearTracks(trackAdapter)
                        showMessage(SearchState.NOTHING_IS_FOUND)
                        Log.d("!@#", response.code().toString())
                        Log.d("!@#", trackList.size.toString())
                    }
                } else {
                    clearTracks(trackAdapter)
                    showMessage(SearchState.UNSUCCESSFUL_CONNECTION)
                    Log.d("!@#", response.code().toString())
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                clearTracks(trackAdapter)
                showMessage(SearchState.UNSUCCESSFUL_CONNECTION)
                Log.d("!@#", "Критическая ошибка")
                Log.d("!@#", t.message.toString())
            }
        })
    }

    private fun clearTracks(adapter: TrackAdapter) {
        adapter.trackList.clear()
        adapter.notifyDataSetChanged()
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

    private fun setSearchVisible() {
        searchLayout.visibility = View.VISIBLE
        searchHistoryLayout.visibility = View.GONE
        clearSearchButton.visibility = View.GONE
    }

    private fun setSearchHistoryVisible() {
        searchLayout.visibility = View.GONE
        searchHistoryLayout.visibility = View.VISIBLE
        clearSearchButton.visibility = View.VISIBLE
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
    }
}

enum class SearchState {
    UNSUCCESSFUL_CONNECTION,
    NOTHING_IS_FOUND,
    SUCCESSFUL_SEARCH
}
