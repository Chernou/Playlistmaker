package com.practicum.playlistmaker.search.presentation.api

import com.practicum.playlistmaker.search.presentation.SearchState

interface SearchTracksView {
    fun render(state: SearchState)
    fun clearSearchText()
    fun hideKeyboard()
    fun clearSearchResult()
    fun showSearchResultLayout()
    fun refreshSearchHistoryAdapter()
}