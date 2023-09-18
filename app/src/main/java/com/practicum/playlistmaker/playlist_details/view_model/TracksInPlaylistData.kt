package com.practicum.playlistmaker.playlist_details.view_model

import com.practicum.playlistmaker.search.domain.model.Track

data class TracksInPlaylistData(
    val duration: String,
    val numberOfTracks: String,
    val tracks: List<Track>
)
