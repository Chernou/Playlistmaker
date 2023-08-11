package com.practicum.playlistmaker.player.view_model

import com.practicum.playlistmaker.playlists.domain.model.Playlist

sealed interface PlaylistsState {
    class DisplayPlaylists(val playlists: List<Playlist>) : PlaylistsState
    object HidePlaylists : PlaylistsState
}