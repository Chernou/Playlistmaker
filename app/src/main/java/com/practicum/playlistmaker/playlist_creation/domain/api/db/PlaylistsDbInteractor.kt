package com.practicum.playlistmaker.playlist_creation.domain.api.db

import com.practicum.playlistmaker.playlist_creation.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsDbInteractor {
    suspend fun addPlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPl(track: Track, playlist: Playlist)
    suspend fun getPlaylist(playlistId: Int): Playlist
    suspend fun editPlaylistUri(playlistId: Int, coverUri: String)
    suspend fun editPlaylistName(playlistId: Int, name: String)
    suspend fun editPlaylistDescription(playlistId: Int, description: String)
}