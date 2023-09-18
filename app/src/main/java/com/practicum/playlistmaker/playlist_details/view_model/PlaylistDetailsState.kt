package com.practicum.playlistmaker.playlist_details.view_model

import com.practicum.playlistmaker.search.domain.model.Track

sealed interface PlaylistDetailsState {
    data class PlaylistScreen(
        val coverUri: String,
        val name: String,
        val description: String,
        val duration: String,
        val numberOfTracks: String,
        val tracks: ArrayList<Track>
    ) : PlaylistDetailsState
}