package com.practicum.playlistmaker.playlists_creation.data.db

import com.practicum.playlistmaker.favorites.data.converters.TrackDbConverter
import com.practicum.playlistmaker.favorites.data.db.AppDatabase
import com.practicum.playlistmaker.playlists_creation.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.playlists_creation.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.playlists_creation.domain.api.db.PlaylistsDbRepository
import com.practicum.playlistmaker.playlists_creation.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsDbRepositoryImpl(
    private val database: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConverter: TrackDbConverter
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

    override suspend fun addTrackToPl(track: Track, playlist: Playlist) {
        database.playlistsDao().updateTracksInPl(
            playlist.id,
            playlistDbConverter.map(playlist).tracks,
            playlist.numberOfTracks
        )
        database.tracksInPlDao().addTrackToPl(trackDbConverter.map(track))
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> =
        playlists.map { playlist -> playlistDbConverter.map(playlist) }
}