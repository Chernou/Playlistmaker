package com.practicum.playlistmaker.player.view_model

import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.utils.Creator
import com.practicum.playlistmaker.player.view_model.api.PlayerView
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.utils.DateUtils.formatTime

class PlayerPresenter(
    private var view: PlayerView?,
    private val track: Track,
) {

    private val interactor = Creator.providePlayerInteractor()
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var playerState = PlayerState.STATE_DEFAULT

    init {
        if (track.previewUrl != null) {
            interactor.preparePlayer(
                trackUri = track.previewUrl,
                onPrepared = {
                    view?.enablePlayImageView()
                    playerState = PlayerState.STATE_PREPARED
                },
                onCompletion = {
                    playerState = PlayerState.STATE_PREPARED
                    mainThreadHandler.removeCallbacks(playbackTimerRunnable)
                    view?.setZeroTimer()
                    view?.setPlayImageView()
                }
            )
        } else {
            view?.noPreviewUrlMessage()
            view?.enablePlayImageView()
        }
    }

    fun backArrowPressed() {
        view?.moveToPreviousScreen()
    }

    fun onPlayPressed() {
        if (track.previewUrl == null) {
            view?.noPreviewUrlMessage()
        } else {
            interactor.startPlayer()
            playbackControl()
        }
    }

    fun onDestroyed() {
        interactor.releasePlayer()
        mainThreadHandler.removeCallbacksAndMessages(null)
        view = null
    }

    fun onPaused() {
        interactor.pausePlayer()
        playerState = PlayerState.STATE_PAUSED
        view?.setPlayImageView()
        mainThreadHandler.removeCallbacks(playbackTimerRunnable)
    }

    private val playbackTimerRunnable = runPlaybackTimer()

    private fun playbackControl() {
        when (playerState) {
            PlayerState.STATE_PLAYING -> {
                onPaused()
            }
            else -> {
                startPlayer()
            }
        }
    }

    private fun startPlayer() {
        interactor.startPlayer()
        playerState = PlayerState.STATE_PLAYING
        view?.setPauseImageView()
        mainThreadHandler.post(runPlaybackTimer())
    }

    private fun runPlaybackTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == PlayerState.STATE_PLAYING) {
                    view?.setPlaybackTime(formatTime(interactor.getPlayerPosition()))
                    mainThreadHandler.postDelayed(this, PLAYBACK_TIME_REFRESH)
                }
            }
        }
    }

    companion object {
        private const val PLAYBACK_TIME_REFRESH = 500L
    }
}