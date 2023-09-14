package com.practicum.playlistmaker.playlists_creation.data.db.entity

import androidx.room.Entity

@Entity(primaryKeys = ["playlistId", "trackId"])
data class PlaylistTracksCrossRef(
    val playlistId: Int,
    val trackId: Int
)