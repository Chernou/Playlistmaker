package com.practicum.playlistmaker.playlist_creation.domain.impl

import com.practicum.playlistmaker.playlist_creation.domain.api.db.PlaylistsDbInteractor
import com.practicum.playlistmaker.playlist_creation.domain.api.db.PlaylistsRepository
import com.practicum.playlistmaker.playlist_creation.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class PlaylistsDbInteractorImpl(private val repository: PlaylistsRepository) :
    PlaylistsDbInteractor {

    override suspend fun addPlaylist(playlist: Playlist) {
        repository.addPlaylist(Dispatchers.IO, playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> = repository.getPlaylists(Dispatchers.IO, )

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        repository.addTrackToPlaylist(Dispatchers.IO, track, playlist)
    }

    override suspend fun getPlaylist(playlistId: Int): Playlist = repository.getPlaylist(Dispatchers.IO, playlistId)
}