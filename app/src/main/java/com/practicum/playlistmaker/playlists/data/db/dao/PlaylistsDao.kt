package com.practicum.playlistmaker.playlists.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.practicum.playlistmaker.playlists.data.db.entity.PlaylistEntity

@Dao
interface PlaylistsDao {

    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists_table")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlists_table WHERE id = :id")
    suspend fun getPlaylist(id: Int): PlaylistEntity

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Transaction
    suspend fun updateTracksInPl(id: Int, tracks: String, numberOfTracks: Int) {
        updateTrackList(id, tracks)
        updateNumberOfTracks(id, numberOfTracks)
    }

    @Query("UPDATE playlists_table SET tracks = :tracks WHERE id = :id")
    suspend fun updateTrackList(id: Int, tracks: String)

    @Query("UPDATE playlists_table SET numberOfTracks = :numberOfTracks WHERE id = :id")
    suspend fun updateNumberOfTracks(id: Int, numberOfTracks: Int)
}