package com.practicum.playlistmaker.search.presentation

import com.practicum.playlistmaker.search.data.SearchHistory
import com.practicum.playlistmaker.search.data.SearchRepository

class SearchPresenter(
    private val view: SearchTracksView,
    private val searchHistory: SearchHistory,
    private val searchRepository: SearchRepository
) {

    fun clearSearchTextPressed() {
        view.clearSearchText()
        view.hideKeyboard()
        view.hideSearchResult()
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
            view.setSearchHistoryVisible()
        } else {
            view.setSearchResultVisible()
        }
        if (searchText != null && searchText.isNotEmpty()) {
            view.saveSearchRequest(searchText)
            view.searchDebounce()
        }
    }

    fun clearSearchHistoryPressed() {
        searchHistory.clearSearchHistory()
        view.setSearchResultVisible()
    }

}