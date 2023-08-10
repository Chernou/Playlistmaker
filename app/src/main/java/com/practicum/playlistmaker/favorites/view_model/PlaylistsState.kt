package com.practicum.playlistmaker.favorites.view_model

import com.practicum.playlistmaker.playlists.domain.model.Playlist

sealed interface PlaylistsState {
    object DisplayEmptyPlaylists : PlaylistsState
    class DisplayPlaylists(val playlists: List<Playlist>) : PlaylistsState
}