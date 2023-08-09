package com.practicum.playlistmaker.playlists.view_model

import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.playlists.domain.api.db.PlaylistsDbInteractor

class PlaylistsViewModel(private val interactor: PlaylistsDbInteractor) : ViewModel() {
    fun coverIsChosen(uri: String) {

    }

    fun onCreatePlClicked() {
    }
}