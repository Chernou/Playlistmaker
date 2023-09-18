package com.practicum.playlistmaker.playlist_details.data

import com.practicum.playlistmaker.favorites.data.converters.TrackDbConverter
import com.practicum.playlistmaker.favorites.data.db.AppDatabase
import com.practicum.playlistmaker.playlist_details.domain.api.PlaylistRepository
import com.practicum.playlistmaker.playlists_creation.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.playlists_creation.domain.model.Playlist
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
                .map { trackInPlEntity -> trackDbConverter.map(trackInPlEntity) }
        }

    override suspend fun getPlaylist(playlistId: Int): Playlist = withContext(Dispatchers.IO) {
        playlistDbConverter.map(database.playlistsDao().getPlaylist(playlistId))
    }
}