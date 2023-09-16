package com.practicum.playlistmaker.playlist_details.data

import com.practicum.playlistmaker.favorites.data.converters.TrackDbConverter
import com.practicum.playlistmaker.favorites.data.db.AppDatabase
import com.practicum.playlistmaker.playlist_details.domain.api.PlaylistRepository
import com.practicum.playlistmaker.playlists_creation.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track

class PlaylistRepositoryImpl(
    private val database: AppDatabase,
    private val trackDbConverter: TrackDbConverter
) : PlaylistRepository {

    override suspend fun getTracksInPlaylist(playlist: Playlist): List<Track> {
        val tracksIds =
            database.playlistsTracksCrossRefDao().getTracksInPlaylist(playlist.playlistId)
                .map { crossRef -> crossRef.trackId }
        return database.tracksInPlDao().getTracks(tracksIds)
            .map { trackInPlEntity -> trackDbConverter.map(trackInPlEntity) }
    }
}