package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.utils.Resource

interface SearchRepository {

    fun searchTracks(query: String): Resource<List<Track>>
    fun getSearchHistory(): ArrayList<Track>
    fun saveSearchHistory(searchHistory: List<Track>)
    fun addTrackToSearchHistory(track: Track)

}