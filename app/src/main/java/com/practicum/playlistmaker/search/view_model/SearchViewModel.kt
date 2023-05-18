package com.practicum.playlistmaker.search.view_model

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.view_model.api.SearchTracksView
import com.practicum.playlistmaker.utils.Creator

class SearchViewModel(
    private val view: SearchTracksView,
    private val router: SearchRouter,
    context: Context
) : ViewModel() {

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2_000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private val interactor = Creator.provideSearchInteractor(context)
    private val handler = Handler(Looper.getMainLooper())

    fun onCreate() {
        if (interactor.getSearchHistory().isNotEmpty()) {
            view.render(SearchState.HistoryContent(interactor.getSearchHistory()))
        } else {
            view.render(SearchState.EmptyScreen)
        }
    }

    fun onClearSearchTextPressed() {
        view.clearSearchText()
        view.hideKeyboard()
        onCreate()
    }

    fun searchEditTextFocusChanged(hasFocus: Boolean, searchText: String?) {
        if (hasFocus && searchText?.isEmpty() == true && interactor.getSearchHistory()
                .isNotEmpty()
        ) {
            view.render(SearchState.HistoryContent(interactor.getSearchHistory()))
        } else {
            view.showSearchResultLayout()
        }
        if (searchText != null && searchText.isNotEmpty()) {
            searchDebounce(searchText)
        }
    }

    fun onClearSearchHistoryPressed() {
        interactor.clearSearchHistory()
        view.refreshSearchHistoryAdapter()
        view.showSearchResultLayout()
    }

    fun onBackArrowPressed() {
        router.goBack()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onRefreshSearchButtonPressed(searchRequest: String) {
        searchRequest(searchRequest)
    }

    fun onTrackPressed(track: Track) {
        interactor.addTrackToSearchHistory(track)
        view.refreshSearchHistoryAdapter()
        router.openTrack(track)
    }

    public override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    private fun searchDebounce(changedText: String) {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun searchRequest(searchText: String) {
        if (searchText.isNotEmpty()) {
            view.render(SearchState.Loading)
            interactor.searchTracks(searchText, object : SearchInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    handler.post {
                        if (foundTracks != null) {
                            if (foundTracks.isNotEmpty()) {
                                view.render(SearchState.SearchContent(foundTracks))
                            } else {
                                view.render(SearchState.EmptySearch)
                            }
                        }
                        if (errorMessage != null) {
                            view.render(SearchState.Error(errorMessage))
                        }
                    }
                }
            })
        }
    }
}