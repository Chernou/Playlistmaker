package com.practicum.playlistmaker.playlists.data.local_files

import android.net.Uri
import com.practicum.playlistmaker.playlists.domain.api.local_files.PlaylistsFilesRepository

class PlaylistsFilesRepositoryImpl(private val privateStorage: PrivateStorage) :
    PlaylistsFilesRepository {
    override suspend fun addToPrivateStorage(uri: Uri, name: String) {
        privateStorage.saveImage(uri, name)
    }
}