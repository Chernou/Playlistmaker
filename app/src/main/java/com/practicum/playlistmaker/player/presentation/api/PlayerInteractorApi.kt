package com.practicum.playlistmaker.player.presentation.api

interface PlayerInteractorApi {

    fun preparePlayer(trackUri: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getPlayerPosition(): Int

}