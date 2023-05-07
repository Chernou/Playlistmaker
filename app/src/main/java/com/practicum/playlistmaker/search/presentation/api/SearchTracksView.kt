package com.practicum.playlistmaker.search.presentation.api

import com.practicum.playlistmaker.search.domain.Track

interface SearchTracksView {
    fun clearSearchText()
    fun hideKeyboard()
    fun clearSearchResult()
    fun showSearchResult(tracks: List<Track>)
    fun showEmptySearch()
    fun showSearchError()
    fun showSearchHistoryLayout()
    fun showSearchResultLayout()
    fun showProgressBar()
    fun executeSearch()
    fun refreshSearchHistoryAdapter()
}