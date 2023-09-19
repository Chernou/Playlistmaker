package com.practicum.playlistmaker.playlist_creation.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.playlist_creation.data.db.entity.TrackInPlEntity

@Dao
interface TracksInPlDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToPl(track: TrackInPlEntity)

    @Query("DELETE FROM tracks_in_pl_table WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Int)

    @Query("SELECT * FROM tracks_in_pl_table WHERE trackId in (:tracksIds)")
    suspend fun getTracks(tracksIds: List<Int>): List<TrackInPlEntity>
}