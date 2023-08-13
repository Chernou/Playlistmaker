package com.practicum.playlistmaker.playlists.domain.model

data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val coverUri: String,
    val tracks: MutableList<Int>,
    val numberOfTracks: Int
) {
    constructor(
        name: String,
        description: String,
        coverPath: String,
    ) : this(0, name, description, coverPath, emptyList<Int>().toMutableList(), 0)
}