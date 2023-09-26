package com.practicum.playlistmaker.favorites.domain.api

import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

interface FavoritesRepository {

    suspend fun addFavorite(coroutineContext: CoroutineContext, track: Track)
    suspend fun deleteFavorite(coroutineContext: CoroutineContext, track: Track)
    fun getFavorites(coroutineContext: CoroutineContext): Flow<List<Track>>
}