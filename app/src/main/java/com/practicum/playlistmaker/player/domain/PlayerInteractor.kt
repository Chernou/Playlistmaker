package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.player.domain.api.PlayerApi
import com.practicum.playlistmaker.player.presentation.api.PlayerInteractorApi

class PlayerInteractor(private val player: PlayerApi) : PlayerInteractorApi {

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
}