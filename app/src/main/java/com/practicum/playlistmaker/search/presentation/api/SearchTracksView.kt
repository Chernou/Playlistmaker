package com.practicum.playlistmaker.search.presentation.api

import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.presentation.SearchState

interface SearchTracksView {
    fun render(state: SearchState)
    fun clearSearchText()
    fun hideKeyboard()
    fun clearSearchResult()
    fun showSearchResult(tracks: List<Track>)
    fun showEmptySearch()
    fun showSearchError()
    fun showSearchHistoryLayout()
    fun showSearchResultLayout()
    fun showProgressBar()
    fun refreshSearchHistoryAdapter()
}