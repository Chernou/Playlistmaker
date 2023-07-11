package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    fun searchTracks(query: String): Flow<Pair<List<Track>?, String?>>
    fun getSearchHistory(): ArrayList<Track>
    fun addTrackToSearchHistory(track: Track)
    fun clearSearchHistory()
}