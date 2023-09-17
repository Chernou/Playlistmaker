package com.practicum.playlistmaker.playlist_details.domain.impl

import com.practicum.playlistmaker.playlist_details.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.playlist_details.domain.api.PlaylistRepository
import com.practicum.playlistmaker.playlists_creation.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track

class PlaylistInteractorImpl(private val repository: PlaylistRepository) : PlaylistInteractor {

    override suspend fun getTracksInPlaylist(playlist: Playlist): List<Track> {
        return repository.getTracksInPlaylist(playlist)
    }

    override suspend fun getPlaylist(playlistId: Int): Playlist {
        return  repository.getPlaylist(playlistId)
    }
}