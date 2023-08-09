package com.practicum.playlistmaker.playlists.domain.api.db

import com.practicum.playlistmaker.playlists.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsDbInteractor {
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
}