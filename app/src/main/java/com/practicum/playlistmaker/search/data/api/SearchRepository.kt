package com.practicum.playlistmaker.search.data.api

import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

interface SearchRepository {
    fun addTrackToSearchHistory(track: Track)
    fun clearSearchHistory()
    fun getSearchHistory(coroutineContext: CoroutineContext): Flow<List<Track>>
    fun searchTracks(coroutineContext: CoroutineContext, query: String): Flow<Resource<List<Track>>>
}