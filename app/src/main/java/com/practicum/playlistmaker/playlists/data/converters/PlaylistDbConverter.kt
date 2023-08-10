package com.practicum.playlistmaker.playlists.data.converters

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.playlists.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.playlists.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track

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
            name = playlist.name,
            description = playlist.description,
            coverUri = playlist.coverUri,
            tracks = gson.fromJson(playlist.tracks, object : TypeToken<ArrayList<Track>>() {}.type)
                ?: ArrayList(),
            numberOfTracks = playlist.numberOfTracks
        )
}