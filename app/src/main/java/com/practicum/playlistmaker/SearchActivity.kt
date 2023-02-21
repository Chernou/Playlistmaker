package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
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

    val trackAdapter = TrackAdapter()

    private lateinit var searchErrorImageView: ImageView
    private lateinit var searchErrorTextView: TextView
    private lateinit var refreshSearchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT).toString()
        }

        searchErrorTextView = findViewById(R.id.search_result_text)
        searchErrorImageView = findViewById(R.id.search_result_image)
        refreshSearchButton = findViewById(R.id.refresh_search_button)

        val toolbar = findViewById<Toolbar>(R.id.search_toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        searchEditText.setText(searchText)
        searchEditText.addTextChangedListener(searchTextWatcher)

        clearImage = findViewById(R.id.clear_image)
        clearImage.setOnClickListener {
            searchText = ""
            searchEditText.setText(searchText)
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            clearImage.visibility = View.INVISIBLE
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        findViewById<RecyclerView?>(R.id.search_recycler_view).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = trackAdapter
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks(searchText)
                true
            }
            false
        }

        refreshSearchButton.setOnClickListener {
            searchTracks(lastUnsuccessfulSearch)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (editable?.isNotEmpty() == true) clearImage.visibility = View.VISIBLE
            else clearImage.visibility = View.INVISIBLE
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
                    if (response.body()?.results?.isNotEmpty() == true) {
                        trackList.clear()
                        trackList.addAll(response.body()?.results!!)
                        trackAdapter.trackList = trackList
                        trackAdapter.notifyDataSetChanged()
                        showMessage(SUCCESSFUL_SEARCH)
                        Log.d("!@#", response.code().toString())
                        Log.d("!@#", searchText)
                        Log.d("!@#", trackList.size.toString())
                    } else {
                        trackAdapter.trackList.clear()
                        trackAdapter.notifyDataSetChanged()
                        showMessage(NOTHING_IS_FOUND)
                        Log.d("!@#", response.code().toString())
                        Log.d("!@#", searchText)
                        Log.d("!@#", trackList.size.toString())
                    }
                } else {
                    trackAdapter.trackList.clear()
                    trackAdapter.notifyDataSetChanged()
                    showMessage(UNSUCCESSFUL_CONNECTION)
                    Log.d("!@#", response.code().toString())
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                showMessage(UNSUCCESSFUL_CONNECTION)
                Log.d("!@#", "Критическая ошибка")
                Log.d("!@#", t.message.toString())
            }
        })
    }

    private fun showMessage(searchState: String) {
        when (searchState) {
            UNSUCCESSFUL_CONNECTION -> {
                searchErrorTextView.visibility = View.VISIBLE
                searchErrorImageView.visibility = View.VISIBLE
                refreshSearchButton.visibility = View.VISIBLE
                searchErrorTextView.text = getString(R.string.no_internet_connection)
                searchErrorImageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.no_internet_connection
                    )
                )
                lastUnsuccessfulSearch = searchText
            }
            NOTHING_IS_FOUND -> {
                searchErrorTextView.visibility = View.VISIBLE
                searchErrorImageView.visibility = View.VISIBLE
                refreshSearchButton.visibility = View.INVISIBLE
                searchErrorTextView.text = getString(R.string.nothing_is_found)
                searchErrorImageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.nothing_is_found
                    )
                )
            }
            else -> {
                searchErrorTextView.visibility = View.INVISIBLE
                searchErrorImageView.visibility = View.INVISIBLE
                refreshSearchButton.visibility = View.INVISIBLE
            }
        }
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val BASE_URL = "https://itunes.apple.com"
        const val UNSUCCESSFUL_CONNECTION = "UNSUCCESSFUL_CONNECTION"
        const val NOTHING_IS_FOUND = "NOTHING_IS_FOUND"
        const val SUCCESSFUL_SEARCH = "SUCCESSFUL_SEARCH"
    }
}

