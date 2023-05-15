package com.practicum.playlistmaker.player.view_model.api

interface PlayerInteractorApi {

    fun preparePlayer(trackUri: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getPlayerPosition(): Int

}