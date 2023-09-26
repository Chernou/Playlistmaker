package com.practicum.playlistmaker.playlist_details.data

import com.practicum.playlistmaker.favorites.data.converters.TrackDbConverter
import com.practicum.playlistmaker.favorites.data.db.AppDatabase
import com.practicum.playlistmaker.playlist_details.domain.api.PlaylistRepository
import com.practicum.playlistmaker.playlist_creation.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.playlist_creation.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class PlaylistRepositoryImpl(
    private val database: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
    private val playlistDbConverter: PlaylistDbConverter
) : PlaylistRepository {

    override suspend fun getTracksInPlaylist(
        coroutineContext: CoroutineContext,
        playlist: Playlist
    ): List<Track> =
        withContext(coroutineContext) {
            val tracksIds =
                database.playlistsTracksCrossRefDao().getTracksInPlaylist(playlist.playlistId)
                    .sortedByDescending { it.addingTime }.map { crossRef -> crossRef.trackId }
            database.tracksInPlDao().getTracks(tracksIds).sortedBy { tracksIds.indexOf(it.trackId) }
                .map { trackInPlEntity ->
                    trackDbConverter.map(
                        trackInPlEntity,
                        checkIsFavorite(coroutineContext, trackInPlEntity.trackId)
                    )
                }
        }

    private suspend fun checkIsFavorite(coroutineContext: CoroutineContext, trackId: Int): Boolean =
        withContext(coroutineContext) {
            database.favoritesDao().isInFavorite(trackId)
        }

    override suspend fun getPlaylist(
        coroutineContext: CoroutineContext,
        playlistId: Int
    ): Playlist = withContext(coroutineContext) {
        playlistDbConverter.map(database.playlistsDao().getPlaylist(playlistId))
    }

    override suspend fun deleteTrackFromPlaylist(
        coroutineContext: CoroutineContext,
        trackId: Int,
        playlistId: Int
    ) {
        withContext(coroutineContext) {
            database.playlistsTracksCrossRefDao()
                .deleteTrack(trackId, playlistId)
            val updatedNumberOfTracks = database.playlistsDao().getNumberOfTracks(playlistId) - 1
            database.playlistsDao().updateNumberOfTracks(playlistId, updatedNumberOfTracks)
            checkAndDeleteTrack(trackId)
        }
    }

    override suspend fun deletePlaylist(coroutineContext: CoroutineContext, playlistId: Int) {
        withContext(coroutineContext) {
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