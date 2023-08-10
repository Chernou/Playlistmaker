package com.practicum.playlistmaker.playlists.data.local_files

import android.net.Uri

interface PrivateStorage {
    suspend fun saveImage(uri: Uri, name: String)
    suspend fun getImage()
}