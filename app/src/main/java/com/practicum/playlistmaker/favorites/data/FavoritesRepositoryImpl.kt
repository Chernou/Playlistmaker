package com.practicum.playlistmaker.favorites.data

import com.practicum.playlistmaker.favorites.data.converters.TrackDbConverter
import com.practicum.playlistmaker.favorites.data.db.AppDatabase
import com.practicum.playlistmaker.favorites.data.db.entity.TrackEntity
import com.practicum.playlistmaker.favorites.domain.api.FavoritesRepository
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
) : FavoritesRepository {

    override suspend fun addFavorite(track: Track) {
        appDatabase.favoritesDao()
            .insertFavorite(trackDbConverter.map(track, Calendar.getInstance().time.time))
    }

    override suspend fun deleteFavorite(track: Track) {
        appDatabase.favoritesDao()
            .deleteFavorite(trackDbConverter.map(track, Calendar.getInstance().time.time))
    }

    override fun getFavorites(): Flow<List<Track>> = flow {
        val tracks = appDatabase.favoritesDao().getFavorites()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> =
        tracks.sortedByDescending { it.addingTime }.map { track -> trackDbConverter.map(track) }

}