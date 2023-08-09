package com.practicum.playlistmaker.playlists.data.converters

import com.practicum.playlistmaker.playlists.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.playlists.domain.model.Playlist

class PlaylistDbConverter {
    fun map(playlist: Playlist): PlaylistEntity =
        PlaylistEntity(
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            tracks = playlist.tracks,
            numberOfTracks = playlist.numberOfTracks
        )

    fun map(playlist: PlaylistEntity): Playlist =
        Playlist(
            playlist.name,
            playlist.description,
            playlist.coverPath,
            playlist.tracks,
            playlist.numberOfTracks
        )
}