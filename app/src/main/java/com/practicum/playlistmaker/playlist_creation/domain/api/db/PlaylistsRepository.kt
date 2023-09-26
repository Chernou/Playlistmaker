package com.practicum.playlistmaker.playlist_creation.domain.api.db

import com.practicum.playlistmaker.playlist_creation.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

interface PlaylistsRepository {

    suspend fun addPlaylist(coroutineContext: CoroutineContext, playlist: Playlist)
    fun getPlaylists(coroutineContext: CoroutineContext): Flow<List<Playlist>>
    suspend fun getPlaylist(coroutineContext: CoroutineContext, playlistId: Int): Playlist

    suspend fun addTrackToPlaylist(
        coroutineContext: CoroutineContext,
        track: Track,
        playlist: Playlist
    )
}