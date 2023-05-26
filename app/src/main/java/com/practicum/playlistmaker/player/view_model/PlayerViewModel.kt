package com.practicum.playlistmaker.player.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.view_model.api.PlayerInteractorApi
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.utils.Creator
import com.practicum.playlistmaker.utils.DateUtils.formatTime

class PlayerViewModel(
    private val track: Track,
    private val interactor: PlayerInteractorApi,
    application: App
) : AndroidViewModel(application) {

    private val handler = Handler(Looper.getMainLooper())
    private val playbackTimerRunnable = runPlaybackTimer()
    private val stateLiveData = MutableLiveData<PlayerState>()
    private val toastStateLive = MutableLiveData<ToastState>()
    private val playbackTimeLive = MutableLiveData<String>()

    public override fun onCleared() {
        interactor.releasePlayer()
        handler.removeCallbacks(playbackTimerRunnable)
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
        handler.removeCallbacks(playbackTimerRunnable)
    }

    fun toastWasShown() {
        toastStateLive.value = ToastState.None
    }

    private fun renderState(playerState: PlayerState) {
        stateLiveData.postValue(playerState)
    }

    private fun showToast() {
        toastStateLive.value =
            ToastState.Show(getApplication<Application>().getString(R.string.no_preview_url))
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
        handler.post(runPlaybackTimer())
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

        fun getViewModelFactory(track: Track): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val interactor = Creator.providePlayerInteractor()
                PlayerViewModel(track, interactor, this[APPLICATION_KEY] as App)
            }
        }
    }
}