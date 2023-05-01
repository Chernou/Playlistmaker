package com.practicum.playlistmaker.search.presentation

import com.practicum.playlistmaker.search.data.SearchHistory
import com.practicum.playlistmaker.search.data.SearchRepository

class SearchPresenter(
    private val view: SearchTracksView,
    private val searchHistory: SearchHistory,
    private val searchRepository: SearchRepository
) {

    fun clearHistoryPressed() {
    }

    fun clearSearchTextPressed() {
        view.clearSearchText()
        view.hideKeyboard()
        view.hideSearchResult()
    }

    fun loadTracks(searchText: String) {
        searchRepository.searchTracks(
            searchRequest = searchText,
            onSuccess = {tracks ->
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
}