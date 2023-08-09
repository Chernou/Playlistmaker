package com.practicum.playlistmaker.playlists.domain.impl

import com.practicum.playlistmaker.playlists.domain.api.db.PlaylistsDbInteractor
import com.practicum.playlistmaker.playlists.domain.api.db.PlaylistsDbRepository
import com.practicum.playlistmaker.playlists.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistsDbInteractorImpl(private val repository: PlaylistsDbRepository) :
    PlaylistsDbInteractor {
    override suspend fun addPlaylist(playlist: Playlist) {
        repository.addPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> = repository.getPlaylists()
}