package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.player.domain.api.Player
import com.practicum.playlistmaker.player.view_model.api.PlayerInteractor

class PlayerInteractorImpl(private val player: Player) : PlayerInteractor {

    override fun preparePlayer(trackUri: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        player.preparePlayer(trackUri, onPrepared, onCompletion)
    }

    override fun startPlayer() {
        player.startPlayer()
    }

    override fun pausePlayer() {
        player.pausePlayer()
    }

    override fun getPlayerPosition(): Int {
        return player.getPlayerPosition()
    }

    override fun releasePlayer() {
        player.releasePlayer()
    }

    override fun isPlaying(): Boolean {
        return player.isPlaying()
    }
}