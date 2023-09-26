package com.practicum.playlistmaker.playlist_details.domain.impl

import com.practicum.playlistmaker.playlist_details.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.playlist_details.domain.api.PlaylistRepository
import com.practicum.playlistmaker.playlist_creation.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Dispatchers

class PlaylistInteractorImpl(private val repository: PlaylistRepository) : PlaylistInteractor {

    override suspend fun getTracksInPlaylist(playlist: Playlist): List<Track> =
        repository.getTracksInPlaylist(Dispatchers.IO, playlist)

    override suspend fun getPlaylist(playlistId: Int): Playlist =
        repository.getPlaylist(Dispatchers.IO, playlistId)

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: Int) {
        repository.deleteTrackFromPlaylist(Dispatchers.IO, trackId, playlistId)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        repository.deletePlaylist(Dispatchers.IO, playlistId)
    }
}