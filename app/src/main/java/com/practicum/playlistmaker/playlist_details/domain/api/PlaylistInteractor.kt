package com.practicum.playlistmaker.playlist_details.domain.api

import com.practicum.playlistmaker.playlists_creation.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track

interface PlaylistInteractor {
    suspend fun getTracksInPlaylist(playlist: Playlist): List<Track>
    suspend fun getPlaylist(playlistId: Int): Playlist
    suspend fun deleteTrackFromPl(trackId: Int, playlistId: Int)
}