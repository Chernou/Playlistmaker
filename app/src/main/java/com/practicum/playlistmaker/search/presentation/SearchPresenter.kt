package com.practicum.playlistmaker.search.presentation

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.annotation.RequiresApi
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.data.SearchHistory
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.presentation.api.SearchTracksView
import com.practicum.playlistmaker.utils.Creator

class SearchPresenter(
    private val view: SearchTracksView,
    private val searchHistory: SearchHistory,
    private val router: SearchRouter,
    private val context: Context
) {

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SHARED_PREFERENCE = "SHARED_PREFERENCE"
        const val SEARCH_DEBOUNCE_DELAY = 2_000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private val interactor = Creator.provideSearchInteractor(context)
    private val handler = Handler(Looper.getMainLooper())

    fun onClearSearchTextPressed() {
        view.clearSearchText()
        view.hideKeyboard()
        view.clearSearchResult()
    }

    fun searchEditTextFocusChanged(hasFocus: Boolean, searchText: String?) {
        if (hasFocus && searchText?.isEmpty() == true && searchHistory.searchHistoryTrackList.isNotEmpty()) {
            view.showSearchHistoryLayout()
        } else {
            view.showSearchResultLayout()
        }
        if (searchText != null && searchText.isNotEmpty()) {
            view.showProgressBar()
            searchDebounce(searchText)
        }
    }

    fun onClearSearchHistoryPressed() {
        searchHistory.clearSearchHistory()
        view.refreshSearchHistoryAdapter()
        view.showSearchResultLayout()
    }

    fun onBackArrowPressed() {
        router.goBack()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onRefreshSearchButtonPressed(searchRequest: String) {
        view.showProgressBar()
        searchRequest(searchRequest)
    }

    fun onTrackPressed(track: Track) {
        searchHistory.addTrack(track)
        view.refreshSearchHistoryAdapter()
        router.openTrack(track)
    }

    private fun searchDebounce(changedText: String) {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchRequest(changedText) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            handler.postDelayed(
                searchRunnable,
                SEARCH_REQUEST_TOKEN,
                SEARCH_DEBOUNCE_DELAY
            )
        } else {
            val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
            handler.postAtTime(
                searchRunnable,
                SEARCH_REQUEST_TOKEN,
                postTime,
            )
        }
    }

    private fun searchRequest(searchText: String) {
        if (searchText.isNotEmpty()) {
            view.showProgressBar()
            interactor.searchTracks(searchText, object : SearchInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    handler.post {
                        if (foundTracks != null) {
                            if (foundTracks.isNotEmpty()) {
                                view.showSearchResult(foundTracks)
                            } else {
                                view.showEmptySearch()
                            }
                        }
                        if (errorMessage != null) {
                            view.showSearchError()
                        }
                    }
                }
            })
        }
    }
}