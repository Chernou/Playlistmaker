package com.practicum.playlistmaker.player.presentation

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.Track
import com.practicum.playlistmaker.utils.DateUtils.formatTime

class PlayerPresenter(
    private val view: PlayerView,
    private val track: Track
) {

    private val mediaPlayer = MediaPlayer()

    init {
        if (track.previewUrl != null) {
            preparePlayer()
        } else {
            view.noPreviewUrlMessage()
            view.enablePlayImageView()
        }
    }

    private var playerState = STATE_DEFAULT
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val PLAYBACK_TIME_REFRESH = 500L
    }

    fun backArrowPressed() {
        view.moveToPreviousScreen()
    }

    fun onPlayPressed() {
        if (track.previewUrl == null) {
            view.noPreviewUrlMessage()
        } else {
            playbackControl()
        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        view.setPauseImageView()
        mainThreadHandler.post(runPlaybackTimer())
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        view.setPlayImageView()
        mainThreadHandler.removeCallbacks(runPlaybackTimer())
    }

    private fun runPlaybackTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    view.setPlaybackTime(formatTime(mediaPlayer.currentPosition))
                    mainThreadHandler.postDelayed(this, PLAYBACK_TIME_REFRESH)
                }
            }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            view.enablePlayImageView()
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            mainThreadHandler.removeCallbacks(runPlaybackTimer())
            view.setZeroTimer()
            view.setPlayImageView()
        }
    }

    fun destroyPlayer() {
        mediaPlayer.release()
        mainThreadHandler.removeCallbacks(runPlaybackTimer())
    }
}