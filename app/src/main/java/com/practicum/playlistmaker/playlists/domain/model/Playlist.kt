package com.practicum.playlistmaker.playlists.domain.model


data class Playlist(
    val name: String,
    val description: String,
    val coverPath: String,
    val tracks: String,
    val numberOfTracks: Int
)