package com.practicum.playlistmaker.playlists.domain.impl

import android.net.Uri
import com.practicum.playlistmaker.playlists.domain.api.local_files.PlaylistsFilesInteractor
import com.practicum.playlistmaker.playlists.domain.api.local_files.PlaylistsFilesRepository

class PlaylistsFilesInteractorImpl(private val filesRepository: PlaylistsFilesRepository) :
    PlaylistsFilesInteractor {
    override suspend fun addToPrivateStorage(uri: Uri, name: String) {
        filesRepository.addToPrivateStorage(uri, name)
    }
}