package com.practicum.playlistmaker.favorites.data.converters

import com.practicum.playlistmaker.favorites.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.Track

class TrackDbConverter {

    fun map(track: TrackEntity): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.country,
            track.releaseDate,
            track.releaseYear,
            track.duration,
            track.lowResArtworkUri,
            track.highResArtworkUri,
            track.genre,
            track.album,
            track.previewUrl,
            true
        )
    }

    fun map(track: Track, addingTime: Long): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.country,
            track.releaseDate,
            track.releaseYear,
            track.duration,
            track.lowResArtworkUri,
            track.highResArtworkUri,
            track.genre,
            track.album,
            track.previewUrl,
            addingTime
        )
    }

}