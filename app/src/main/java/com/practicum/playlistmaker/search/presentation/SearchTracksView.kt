package com.practicum.playlistmaker.search.presentation

import com.practicum.playlistmaker.Track

interface SearchTracksView {
    fun clearSearchText()
    fun hideKeyboard()
    fun clearSearchResult()
    fun showSearchResult(tracks: List<Track>)
    fun showEmptySearch()
    fun showSearchError()
    fun showSearchHistoryLayout()
    fun showSearchResultLayout()
    fun moveToPreviousScreen()
    fun showProgressBar()

    fun executeSearch()
}