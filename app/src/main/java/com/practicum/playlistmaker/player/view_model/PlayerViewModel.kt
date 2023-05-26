package com.practicum.playlistmaker.player.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.view_model.api.PlayerInteractor
import com.practicum.playlistmaker.search.data.api.ResourceProvider
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.utils.DateUtils.formatTime

class PlayerViewModel(
    private val track: Track,
    private val resourceProvider: ResourceProvider,
    private val interactor: PlayerInteractor,
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private val playbackTimerRunnable = runPlaybackTimer()
    private val stateLiveData = MutableLiveData<PlayerState>()
    private val toastStateLive = MutableLiveData<ToastState>()
    private val playbackTimeLive = MutableLiveData<String>()

    public override fun onCleared() {
        interactor.releasePlayer()
        handler.removeCallbacksAndMessages(PLAYER_REQUEST_TOKEN)
    }

    fun preparePlayer() {
        if (track.previewUrl != null) {
            interactor.preparePlayer(
                trackUri = track.previewUrl,
                onPrepared = {
                    renderState(PlayerState.PreparedState)
                },
                onCompletion = {
                    handler.removeCallbacks(playbackTimerRunnable)
                    renderState(PlayerState.PreparedState)
                }
            )
        } else {
            showToast()
            renderState(PlayerState.PreparedState)
        }
    }

    fun observeState(): LiveData<PlayerState> = stateLiveData
    fun observeToastState(): LiveData<ToastState> = toastStateLive
    fun observePlaybackTime(): LiveData<String> = playbackTimeLive

    fun onPlayPressed() {
        if (track.previewUrl == null) {
            showToast()
        } else {
            playbackControl()
        }
    }

    fun onPaused() {
        interactor.pausePlayer()
        renderState(PlayerState.PauseState)
        handler.removeCallbacksAndMessages(PLAYER_REQUEST_TOKEN)
    }

    fun toastWasShown() {
        toastStateLive.value = ToastState.None
    }

    private fun renderState(playerState: PlayerState) {
        stateLiveData.postValue(playerState)
    }

    private fun showToast() {
        toastStateLive.value =
            ToastState.Show(resourceProvider.getString(R.string.no_preview_url))
    }

    private fun setPlaybackTime(playbackTime: String) {
        playbackTimeLive.value = playbackTime
    }

    private fun playbackControl() {
        if (interactor.isPlaying()) {
            onPaused()
        } else {
            startPlayer()
        }
    }

    private fun startPlayer() {
        interactor.startPlayer()
        renderState(PlayerState.PlayingState)
        handler.postAtTime(runPlaybackTimer(), PLAYER_REQUEST_TOKEN, ZERO_MILLIS)
    }

    private fun runPlaybackTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                if (interactor.isPlaying()) {
                    setPlaybackTime(formatTime(interactor.getPlayerPosition()))
                    handler.postDelayed(this, PLAYBACK_TIME_REFRESH)
                }
            }
        }
    }

    companion object {
        private const val PLAYBACK_TIME_REFRESH = 500L
        private const val ZERO_MILLIS = 500L
        private val PLAYER_REQUEST_TOKEN = Any()
    }
}