package com.practicum.playlistmaker.playlist_creation.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.practicum.playlistmaker.playlist_creation.data.db.entity.PlaylistEntity

@Dao
interface PlaylistsDao {

    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists_table")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlists_table WHERE playlistId = :playlistId")
    suspend fun getPlaylist(playlistId: Int): PlaylistEntity

    @Query("DELETE FROM playlists_table WHERE playlistId = :playlistId")
    suspend fun deletePlaylist(playlistId: Int)

    @Query("UPDATE playlists_table SET numberOfTracks = :numberOfTracks WHERE playlistId = :playlistId")
    suspend fun updateNumberOfTracks(playlistId: Int, numberOfTracks: Int)

    @Query("SELECT numberOfTracks FROM playlists_table WHERE playlistId = :playlistId")
    suspend fun getNumberOfTracks(playlistId: Int): Int

    @Query("UPDATE playlists_table SET coverUri = :coverUri WHERE playlistId = :playlistId")
    suspend fun updateUri(playlistId: Int, coverUri: String)

    @Query("UPDATE playlists_table SET name = :name WHERE playlistId = :playlistId")
    suspend fun updateName(playlistId: Int, name: String)

    @Query("UPDATE playlists_table SET description = :description WHERE playlistId = :playlistId")
    suspend fun updateDescription(playlistId: Int, description: String)
}