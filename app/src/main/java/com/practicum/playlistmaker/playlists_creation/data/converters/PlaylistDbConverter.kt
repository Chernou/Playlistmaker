package com.practicum.playlistmaker.playlists_creation.data.converters

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.playlists_creation.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.playlists_creation.domain.model.Playlist

class PlaylistDbConverter(private val gson: Gson) {
    fun map(playlist: Playlist): PlaylistEntity =
        PlaylistEntity(
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverUri,
            tracks = Gson().toJson(playlist.tracks),
            numberOfTracks = playlist.numberOfTracks
        )

    fun map(playlist: PlaylistEntity): Playlist =
        Playlist(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverUri = playlist.coverUri,
            tracks = gson.fromJson(playlist.tracks, object : TypeToken<ArrayList<Int>>() {}.type)
                ?: ArrayList(),
            numberOfTracks = playlist.numberOfTracks
        )
}