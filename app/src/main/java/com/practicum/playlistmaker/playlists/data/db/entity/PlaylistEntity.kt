package com.practicum.playlistmaker.playlists.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val description: String,
    val coverPath: String,
    val tracks: String,
    val numberOfTracks: Int
) {
    constructor(
        name: String,
        description: String,
        coverPath: String,
        tracks: String,
        numberOfTracks: Int
    ) : this(0, name, description, coverPath, tracks, numberOfTracks)

}
