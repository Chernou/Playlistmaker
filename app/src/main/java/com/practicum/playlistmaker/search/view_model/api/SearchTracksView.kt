package com.practicum.playlistmaker.search.view_model.api

import com.practicum.playlistmaker.search.view_model.SearchState

interface SearchTracksView {
    fun render(state: SearchState)
    fun clearSearchText()
    fun hideKeyboard()
    fun clearSearchResult()
    fun showSearchResultLayout()
    fun refreshSearchHistoryAdapter()
}