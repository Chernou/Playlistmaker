package com.practicum.playlistmaker.favorites.domain.impl

import com.practicum.playlistmaker.favorites.domain.api.FavoritesInteractor
import com.practicum.playlistmaker.favorites.domain.api.FavoritesRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val repository: FavoritesRepository) : FavoritesInteractor {

    override suspend fun addFavorite(track: Track) {
        repository.addFavorite(Dispatchers.IO, track)
    }

    override suspend fun deleteFavorite(track: Track) {
        repository.deleteFavorite(Dispatchers.IO, track)
    }

    override fun getFavorites(): Flow<List<Track>> = repository.getFavorites(Dispatchers.IO)

}