package com.practicum.playlistmaker.playlists.view_model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlists.domain.api.db.PlaylistsDbInteractor
import com.practicum.playlistmaker.playlists.domain.api.local_files.PlaylistsFilesInteractor
import com.practicum.playlistmaker.playlists.domain.model.Playlist
import kotlinx.coroutines.launch

class PlaylistsCreationViewModel(
    private val dbInteractor: PlaylistsDbInteractor,
    private val filesInteractor: PlaylistsFilesInteractor
) : ViewModel() {

    fun onCreatePlClicked(name: String, description: String?, uri: Uri?) {
        viewModelScope.launch {
            dbInteractor.addPlaylist(Playlist(name, description ?: "", uri?.path ?: ""))
            if (uri != null) {
                filesInteractor.addToPrivateStorage(uri, name)
            }
        }
    }
}