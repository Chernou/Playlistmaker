package com.practicum.playlistmaker.playlist_details.domain.api

import com.practicum.playlistmaker.playlist_creation.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlin.coroutines.CoroutineContext

interface PlaylistRepository {

    suspend fun deletePlaylist(coroutineContext: CoroutineContext, playlistId: Int)
    suspend fun getPlaylist(coroutineContext: CoroutineContext, playlistId: Int): Playlist

    suspend fun deleteTrackFromPlaylist(
        coroutineContext: CoroutineContext,
        trackId: Int,
        playlistId: Int
    )

    suspend fun getTracksInPlaylist(
        coroutineContext: CoroutineContext,
        playlist: Playlist
    ): List<Track>
}