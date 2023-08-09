package com.practicum.playlistmaker.playlists.data.db

import com.practicum.playlistmaker.favorites.data.db.AppDatabase
import com.practicum.playlistmaker.playlists.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.playlists.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.playlists.domain.api.db.PlaylistsDbRepository
import com.practicum.playlistmaker.playlists.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsDbRepositoryImpl(
    private val database: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter
) : PlaylistsDbRepository {
    override suspend fun addPlaylist(playlist: Playlist) {
        database.playlistsDao().insertPlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        database.playlistsDao().deletePlaylist(playlistDbConverter.map(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = database.playlistsDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> =
        playlists.map { playlist -> playlistDbConverter.map(playlist) }
}