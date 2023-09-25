package com.practicum.playlistmaker.playlist_details.data

import com.practicum.playlistmaker.favorites.data.converters.TrackDbConverter
import com.practicum.playlistmaker.favorites.data.db.AppDatabase
import com.practicum.playlistmaker.playlist_details.domain.api.PlaylistRepository
import com.practicum.playlistmaker.playlist_creation.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.playlist_creation.data.db.entity.PlaylistTracksCrossRef
import com.practicum.playlistmaker.playlist_creation.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(
    private val database: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
    private val playlistDbConverter: PlaylistDbConverter
) : PlaylistRepository {

    override suspend fun getTracksInPlaylist(playlist: Playlist): List<Track> =
        withContext(Dispatchers.IO) {
            val tracksIds =
                database.playlistsTracksCrossRefDao().getTracksInPlaylist(playlist.playlistId)
                    .map { crossRef -> crossRef.trackId }
            database.tracksInPlDao().getTracks(tracksIds)
                .map { trackInPlEntity -> trackDbConverter.map(trackInPlEntity, checkIsFavorite(trackInPlEntity.trackId)) }
        }

    private fun checkIsFavorite(trackId: Int): Boolean {
        return database.favoritesDao().isInFavorite(trackId)
    }

    override suspend fun getPlaylist(playlistId: Int): Playlist = withContext(Dispatchers.IO) {
        playlistDbConverter.map(database.playlistsDao().getPlaylist(playlistId))
    }

    override suspend fun deleteTrackFromPl(trackId: Int, playlistId: Int) {
        withContext(Dispatchers.IO) {
            database.playlistsTracksCrossRefDao()
                .deleteTrack(PlaylistTracksCrossRef(trackId, playlistId))
            val updatedNumberOfTracks = database.playlistsDao().getNumberOfTracks(playlistId) - 1
            database.playlistsDao().updateNumberOfTracks(playlistId, updatedNumberOfTracks)
            checkAndDeleteTrack(trackId)
        }
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        withContext(Dispatchers.IO) {
            database.playlistsDao().deletePlaylist(playlistId)
            val tracksInPlaylist =
                database.playlistsTracksCrossRefDao().getTracksInPlaylist(playlistId)
            database.playlistsTracksCrossRefDao().deleteAllTracksInPlaylist(playlistId)
            for (track in tracksInPlaylist) {
                checkAndDeleteTrack(track.trackId)
            }
        }
    }

    private suspend fun checkAndDeleteTrack(trackId: Int) {
        if (database.playlistsTracksCrossRefDao().getPlaylistsContainingTrack(trackId)
                .isEmpty()
        ) database.tracksInPlDao().deleteTrack(trackId)
    }
}