package com.practicum.playlistmaker.playlists.domain.api.local_files

import android.net.Uri

interface PlaylistsFilesInteractor {
    suspend fun addToPrivateStorage(uri: Uri, name: String)
}