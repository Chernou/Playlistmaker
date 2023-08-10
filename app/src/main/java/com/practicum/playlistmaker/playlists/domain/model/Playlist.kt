package com.practicum.playlistmaker.playlists.domain.model

import com.practicum.playlistmaker.search.domain.model.Track


data class Playlist(
    val name: String,
    val description: String,
    val coverUri: String,
    val tracks: List<Track>,
    val numberOfTracks: Int
) {
    constructor(
        name: String,
        description: String,
        coverPath: String,
    ) : this(name, description, coverPath, emptyList<Track>(), 0)
}