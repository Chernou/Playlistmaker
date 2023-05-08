package com.practicum.playlistmaker.search.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.data.SearchHistory
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.presentation.api.SearchTracksView

class SearchPresenter(
    private val view: SearchTracksView,
    private val searchHistory: SearchHistory,
    private val router: SearchRouter,
    private val interactor: SearchInteractor
) {

    fun onClearSearchTextPressed() {
        view.clearSearchText()
        view.hideKeyboard()
        view.clearSearchResult()
    }

    fun loadTracks(searchText: String) {
        interactor.searchTracks(searchText, object : SearchInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
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
        })
    }

    /*fun loadTracks(searchText: String) {
        interactor.searchTracks(searchText, object : SearchInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>) {
                if (foundTracks.isNotEmpty()) {
                    view.showSearchResult(foundTracks)
                } else {
                    view.showEmptySearch()
                }
            }
        })
    }*/

    fun searchEditTextFocusChanged(hasFocus: Boolean, searchText: String?) {
        if (hasFocus && searchText?.isEmpty() == true && searchHistory.searchHistoryTrackList.isNotEmpty()) {
            view.showSearchHistoryLayout()
        } else {
            view.showSearchResultLayout()
        }
        if (searchText != null && searchText.isNotEmpty()) {
            view.showProgressBar()
            view.executeSearch()
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
        loadTracks(searchRequest)
    }

    fun onTrackPressed(track: Track) {
        searchHistory.addTrack(track)
        view.refreshSearchHistoryAdapter()
        router.openTrack(track)
    }

}