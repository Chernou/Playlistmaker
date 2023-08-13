package com.practicum.playlistmaker.playlists.domain.api.db

import com.practicum.playlistmaker.playlists.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsDbRepository {

    suspend fun addPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPl(track: Track, playlist: Playlist)
}