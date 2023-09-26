package com.practicum.playlistmaker.playlist_creation.data.local_files

import android.net.Uri
import com.practicum.playlistmaker.playlist_creation.domain.api.local_files.PlaylistsFilesRepository
import kotlinx.coroutines.Dispatchers
import java.net.URI

class PlaylistsFilesRepositoryImpl(private val privateStorage: PrivateStorage) :
    PlaylistsFilesRepository {

    override suspend fun addToPrivateStorage(uri: Uri): URI = privateStorage.saveImage(Dispatchers.IO, uri)

    override suspend fun deleteFromPrivateStorage(coverUri: String) {
        privateStorage.deleteFromPrivateStorage(Dispatchers.IO, coverUri)
    }
}