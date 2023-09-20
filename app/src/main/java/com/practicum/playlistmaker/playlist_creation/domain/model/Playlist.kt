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

    override fun toString(): String {
        return Gson().toJson(this)
    }

    companion object {
        val emptyPlaylist = Playlist(0, "", "", "", ArrayList(), 0)
    }
}