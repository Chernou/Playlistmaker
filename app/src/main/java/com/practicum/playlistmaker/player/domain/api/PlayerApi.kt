package com.practicum.playlistmaker.player.domain.api

interface PlayerApi {

    fun preparePlayer(trackUri: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getPlayerPosition(): Int

}