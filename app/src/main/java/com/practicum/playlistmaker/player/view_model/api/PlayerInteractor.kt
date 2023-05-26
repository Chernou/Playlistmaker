package com.practicum.playlistmaker.player.view_model.api

interface PlayerInteractor {

    fun preparePlayer(trackUri: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getPlayerPosition(): Int
    fun isPlaying(): Boolean

}