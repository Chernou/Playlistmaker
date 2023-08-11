package com.practicum.playlistmaker.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.favorites.domain.api.FavoritesInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.playlists.domain.api.db.PlaylistsDbInteractor
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.utils.DateUtils.formatTime
import com.practicum.playlistmaker.utils.ResourceProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val resourceProvider: ResourceProvider,
    private val playerInteractor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistsDbInteractor: PlaylistsDbInteractor
) : ViewModel() {

    private val isFavoriteLiveData = MutableLiveData<Boolean>()

    init {
        isFavoriteLiveData.value = track.isFavorite
    }

    private val stateLiveData = MutableLiveData<PlayerState>()
    private val toastStateLiveData = MutableLiveData<ToastState>()
    private val playlistsLiveData = MutableLiveData<PlaylistsState>()
    private var timerJob: Job? = null

    public override fun onCleared() {
        playerInteractor.releasePlayer()
        timerJob?.cancel()
        stateLiveData.value = PlayerState.DefaultState()
    }

    fun preparePlayer() {
        if (track.previewUrl != null) {
            playerInteractor.preparePlayer(
                trackUri = track.previewUrl,
                onPrepared = {
                    renderState(PlayerState.PreparedState())
                },
                onCompletion = {
                    timerJob?.cancel()
                    renderState(PlayerState.PreparedState())
                }
            )
        } else {
            showToast()
            renderState(PlayerState.PreparedState())
        }
    }

    fun observeState(): LiveData<PlayerState> = stateLiveData
    fun observeToastState(): LiveData<ToastState> = toastStateLiveData
    fun observeIsFavorite(): LiveData<Boolean> = isFavoriteLiveData
    fun observePlaylists(): LiveData<PlaylistsState> = playlistsLiveData

    fun onPlayPressed() {
        if (track.previewUrl == null) {
            showToast()
        } else {
            playbackControl()
        }
    }

    fun toastWasShown() {
        toastStateLiveData.value = ToastState.None
    }

    fun onPause() {
        pausePlayer()
    }

    fun onFavoriteClicked() {
        isFavoriteLiveData.value = !track.isFavorite
        viewModelScope.launch {
            if (track.isFavorite) {
                favoritesInteractor.deleteFavorite(track)
                track.isFavorite = false
            } else {
                favoritesInteractor.addFavorite(track)
                track.isFavorite = true
            }
        }
    }

    fun addToPlaylistClicked() {
        viewModelScope.launch {
            playlistsDbInteractor.getPlaylists().collect {
                playlistsLiveData.postValue(PlaylistsState.DisplayPlaylists(it))
            }
        }
    }

    private fun renderState(playerState: PlayerState) {
        stateLiveData.postValue(playerState)
    }

    private fun showToast() {
        toastStateLiveData.value =
            ToastState.Show(resourceProvider.getString(R.string.no_preview_url))
    }

    private fun playbackControl() {
        when (stateLiveData.value) {
            is PlayerState.PlayingState -> pausePlayer()
            is PlayerState.PauseState, is PlayerState.PreparedState -> startPlayer()
            else -> {}
        }
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        renderState(PlayerState.PlayingState(getCurrentPlayerPosition()))
        startTimer()
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        timerJob?.cancel()
        renderState(PlayerState.PauseState(getCurrentPlayerPosition()))
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(PLAYBACK_TIME_REFRESH)
                stateLiveData.postValue(PlayerState.PlayingState(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return formatTime(playerInteractor.getPlayerPosition())
    }

    companion object {
        private const val PLAYBACK_TIME_REFRESH = 200L
    }
}