package com.practicum.playlistmaker.search.presentation

import com.practicum.playlistmaker.Track

interface SearchTracksView {
    fun clearSearchText()
    fun hideKeyboard()
    fun hideSearchResult()
    fun showSearchResult(tracks: List<Track>)
    fun showEmptySearch()
    fun showSearchError()

}