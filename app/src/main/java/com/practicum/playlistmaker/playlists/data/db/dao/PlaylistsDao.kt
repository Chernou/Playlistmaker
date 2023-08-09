package com.practicum.playlistmaker.playlists.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.practicum.playlistmaker.playlists.data.db.entity.PlaylistEntity

@Dao
interface PlaylistsDao {

    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists_table")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)
}