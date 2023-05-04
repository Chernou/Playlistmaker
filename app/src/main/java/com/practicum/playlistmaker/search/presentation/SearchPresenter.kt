package com.practicum.playlistmaker.search.presentation

import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.data.SearchHistory
import com.practicum.playlistmaker.search.data.SearchRepository
import com.practicum.playlistmaker.search.presentation.api.SearchTracksView

class SearchPresenter(
    private val view: SearchTracksView,
    private val searchHistory: SearchHistory,
    private val searchRepository: SearchRepository,
    private val router: SearchRouter
) {

    fun onClearSearchTextPressed() {
        view.clearSearchText()
        view.hideKeyboard()
        view.clearSearchResult()
    }

    fun loadTracks(searchText: String) {
        searchRepository.searchTracks(
            searchRequest = searchText,
            onSuccess = { tracks ->
                if (tracks.isNotEmpty()) {
                    view.showSearchResult(tracks)
                } else {
                    view.showEmptySearch()
                }
            },
            onFailure = {
                view.showSearchError()
            }
        )
    }

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