package com.practicum.playlistmaker.playlists_creation.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.playlists_creation.data.db.entity.PlaylistTracksCrossRef

@Dao
interface PlaylistTracksCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToPl(trackInPlaylist: PlaylistTracksCrossRef)

    @Query("SELECT * FROM playlist_tracks_cross_ref_table WHERE playlistId = :playlistId")
    suspend fun getTracksInPlaylist(playlistId: Int): List<PlaylistTracksCrossRef>

    @Delete
    suspend fun deleteTrack(track: PlaylistTracksCrossRef)

    @Query("SELECT * FROM playlist_tracks_cross_ref_table WHERE trackId = :trackId")
    suspend fun getPlaylistsContainingTrack(trackId: Int): List<PlaylistTracksCrossRef>
}