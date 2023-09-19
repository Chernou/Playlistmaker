package com.practicum.playlistmaker.playlist_details.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist_details.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.playlist_creation.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.utils.DateUtils.getMinutesFromMillis
import com.practicum.playlistmaker.utils.TextUtils
import com.practicum.playlistmaker.utils.TextUtils.getNumberOfTracksString
import com.practicum.playlistmaker.utils.TextUtils.getTotalMinutesString
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    private val playlistId: Int,
    private val interactor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private lateinit var playlist: Playlist
    private val tracks = ArrayList<Track>()

    init {
        renderScreen()
    }

    private var trackToDelete: Track? = null

    private val playlistLiveData = MutableLiveData<PlaylistDetails>()
    fun observePlaylistData(): LiveData<PlaylistDetails> = playlistLiveData

    private val tracksLiveData = MutableLiveData<TracksInPlaylistData>()
    fun observeTracksLiveDate(): LiveData<TracksInPlaylistData> = tracksLiveData

    private val toastLiveData = MutableLiveData<EmptyPlaylistToastState>()
    fun observeToastLiveData(): LiveData<EmptyPlaylistToastState> = toastLiveData

    private val menuStateLiveData = MutableLiveData<PlaylistMenuState>()
    fun observePlaylistMenuState(): LiveData<PlaylistMenuState> = menuStateLiveData

    private fun renderScreen() {
        viewModelScope.launch {
            getPlaylistById()
            initTrackList()
            renderPlaylistData()
            renderTracksData()
        }
    }

    private suspend fun getPlaylistById() {
        playlist = interactor.getPlaylist(playlistId)
    }

    private suspend fun initTrackList() {
        tracks.addAll(interactor.getTracksInPlaylist(playlist))
    }

    private fun renderPlaylistData() {
        playlistLiveData.value = PlaylistDetails(
            playlist.coverUri,
            playlist.name,
            playlist.description,
        )
    }

    private fun getDuration(): String {
        var durationInMillis = 0
        for (track in tracks) {
            durationInMillis += track.duration
        }
        return getTotalMinutesString(getMinutesFromMillis(durationInMillis))
    }

    private fun getNumberOfTracks(): String = getNumberOfTracksString(tracks.size)

    private fun renderTracksData() {
        tracksLiveData.value = TracksInPlaylistData(getDuration(), getNumberOfTracks(), tracks)
    }

    fun onTrackLongClicked(track: Track) {
        trackToDelete = track
    }

    fun onTrackDeleteConfirmed() {
        viewModelScope.launch {
            interactor.deleteTrackFromPl(trackToDelete!!.trackId, playlistId)
        }
        tracks.remove(trackToDelete)
        trackToDelete = null
        renderTracksData()
    }

    fun onShareClicked() {
        if (tracks.isEmpty()) toastLiveData.value = EmptyPlaylistToastState.SHOW
        else sharingInteractor.shareString(TextUtils.getSharedTracksString(tracks))
    }

    fun onMenuClicked() {
        menuStateLiveData.value = PlaylistMenuState.SHOW
    }

    fun toastWasShown() {
        toastLiveData.value = EmptyPlaylistToastState.NONE
    }

    fun onPlaylistDeleteConfirmed() {
        viewModelScope.launch {
            interactor.deletePlaylist(playlistId)
        }
    }

    fun menuWasShown() {
        menuStateLiveData.value = PlaylistMenuState.NONE
    }
}