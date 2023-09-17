package com.practicum.playlistmaker.playlist_details.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist_details.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.playlists_creation.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    private val playlistId: Int,
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private var tracksDuration = ""
    private var numberOfTracks = ""
    private val tracks = ArrayList<Track>()
    private lateinit var playlist: Playlist

    init {
        getPlaylistFromDb()
        initialiseTrackList()
    }

    private fun getPlaylistFromDb() {
        viewModelScope.launch {
            playlist = interactor.getPlaylist(playlistId)
        }
    }

    private fun initialiseTrackList() {
        viewModelScope.launch {
            tracks.addAll(interactor.getTracksInPlaylist(playlist))
        }
    }
}