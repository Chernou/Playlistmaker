package com.practicum.playlistmaker.playlist_details.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist_details.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.playlists_creation.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    private val playlist: Playlist,
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private var tracksDuration = ""
    private var numberOfTracks = ""
    private val tracks = ArrayList<Track>()

    init {
        initialiseTracks()
    }

    private fun initialiseTracks() {
        viewModelScope.launch {
            tracks.addAll(interactor.getTracksInPlaylist(playlist))
        }
    }
}