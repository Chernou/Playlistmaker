package com.practicum.playlistmaker.playlist_details.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist_details.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.playlists_creation.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.utils.DateUtils.getMinutesFromMillis
import com.practicum.playlistmaker.utils.TextUtils.getNumberOfTracksString
import com.practicum.playlistmaker.utils.TextUtils.getTotalMinutesString
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    private val playlistId: Int,
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private lateinit var playlist: Playlist
    private val tracks = ArrayList<Track>()

    init {
        renderScreen()
    }

    private val playlistStateLiveData = MutableLiveData<PlaylistDetailsState>()
    fun observePlaylistState(): LiveData<PlaylistDetailsState> = playlistStateLiveData

    private fun renderScreen() {
        viewModelScope.launch {
            getPlaylistById()
            initTrackList()
            playlistStateLiveData.value = PlaylistDetailsState.PlaylistScreen(
                playlist.coverUri,
                playlist.name,
                playlist.description,
                getDuration(),
                getNumberOfTracks(),
                tracks
            )
        }
    }

    private suspend fun getPlaylistById() {
        playlist = interactor.getPlaylist(playlistId)
    }

    private suspend fun initTrackList() {
        tracks.addAll(interactor.getTracksInPlaylist(playlist))
    }

    private fun getDuration(): String {
        var durationInMillis = 0
        for (track in tracks) {
            durationInMillis += track.duration
        }
        return getTotalMinutesString(getMinutesFromMillis(durationInMillis))
    }

    private fun getNumberOfTracks(): String {
        return getNumberOfTracksString(tracks.size)
    }
}