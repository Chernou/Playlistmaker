package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.Player

class PlayerImpl(private val mediaPlayer: MediaPlayer) : Player {

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

    override fun getPlayerPosition() = mediaPlayer.currentPosition

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun isPlaying() = mediaPlayer.isPlaying
}