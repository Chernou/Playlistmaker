package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.PlayerApi

class Player : PlayerApi {

    private val mediaPlayer = MediaPlayer()

    override fun preparePlayer(trackUri: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer.setDataSource(trackUri)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onPrepared.invoke()
        }
        mediaPlayer.setOnCompletionListener {
            onCompletion.invoke()
        }
    }

    override fun getPlayerPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }
}