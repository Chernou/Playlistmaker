package com.practicum.playlistmaker.favorites.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.favorites.data.db.dao.FavoritesDao
import com.practicum.playlistmaker.favorites.data.db.entity.TrackEntity
import com.practicum.playlistmaker.playlists.data.db.dao.PlaylistsDao
import com.practicum.playlistmaker.playlists.data.db.entity.PlaylistEntity

@Database(version = 1, entities = [TrackEntity::class, PlaylistEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao
    abstract fun playlistsDao(): PlaylistsDao
}