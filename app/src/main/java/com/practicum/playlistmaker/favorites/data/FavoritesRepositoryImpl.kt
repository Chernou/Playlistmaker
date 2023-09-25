package com.practicum.playlistmaker.favorites.data

import com.practicum.playlistmaker.favorites.data.converters.TrackDbConverter
import com.practicum.playlistmaker.favorites.data.db.AppDatabase
import com.practicum.playlistmaker.favorites.data.db.entity.FavoritesTrackEntity
import com.practicum.playlistmaker.favorites.domain.api.FavoritesRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.Calendar

class FavoritesRepositoryImpl(
    private val database: AppDatabase,
    private val trackDbConverter: TrackDbConverter
) : FavoritesRepository {

    override suspend fun addFavorite(track: Track) {
        withContext(Dispatchers.IO) {
            database.favoritesDao()
                .insertFavorite(trackDbConverter.map(track, Calendar.getInstance().time.time))
        }
    }

    override suspend fun deleteFavorite(track: Track) {
        withContext(Dispatchers.IO) {
            database.favoritesDao().deleteFavorite(trackDbConverter.map(track, Calendar.getInstance().time.time))
        }
    }

    override fun getFavorites(): Flow<List<Track>> = flow {
        val tracks = database.favoritesDao().getFavorites()
        emit(convertFromTrackEntity(tracks))
    }.flowOn(Dispatchers.IO)

    private fun convertFromTrackEntity(tracks: List<FavoritesTrackEntity>): List<Track> =
        tracks.sortedByDescending { it.addingTime }.map { track -> trackDbConverter.map(track) }
}