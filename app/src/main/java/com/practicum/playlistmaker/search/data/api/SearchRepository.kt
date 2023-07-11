package com.practicum.playlistmaker.search.data.api

import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    fun searchTracks(query: String): Flow<Resource<List<Track>>>
    fun getSearchHistory(): ArrayList<Track>
    fun addTrackToSearchHistory(track: Track)
    fun clearSearchHistory()

}