package com.practicum.playlistmaker.playlists.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.playlists.data.db.entity.TrackInPlEntity

@Dao
interface TracksInPlDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToPl(track: TrackInPlEntity)

    @Query("SELECT * FROM tracks_in_pl_table")
    suspend fun getTracksInPl(): List<TrackInPlEntity>

    @Delete
    suspend fun deleteTrackFromPl(track: TrackInPlEntity)
}