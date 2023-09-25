package com.practicum.playlistmaker.playlist_creation.domain.model

import com.google.gson.Gson

data class Playlist(
    val playlistId: Int,
    val name: String,
    val description: String,
    val coverUri: String,
    val tracks: ArrayList<Int>,
    val numberOfTracks: Int
) {

    constructor() :  this(0, "", "", "", ArrayList(), 0)

    override fun toString(): String {
        return Gson().toJson(this)
    }
}