package com.practicum.playlistmaker.favorites.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.favorites.data.db.dao.FavoritesDao
import com.practicum.playlistmaker.favorites.data.db.entity.TrackEntity
import com.practicum.playlistmaker.playlists.data.db.dao.PlaylistsDao
import com.practicum.playlistmaker.playlists.data.db.dao.TracksInPlDao
import com.practicum.playlistmaker.playlists.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.playlists.data.db.entity.TrackInPlEntity

@Database(version = 1, entities = [TrackEntity::class, PlaylistEntity::class, TrackInPlEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao
    abstract fun playlistsDao(): PlaylistsDao
    abstract fun tracksInPlDao(): TracksInPlDao
}