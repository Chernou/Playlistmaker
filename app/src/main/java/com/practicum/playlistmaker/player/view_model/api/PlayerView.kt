package com.practicum.playlistmaker.player.view_model.api

interface PlayerView {
    fun moveToPreviousScreen()
    fun noPreviewUrlMessage()
    fun setPlaybackTime(time: String)
    fun setPauseImageView()
    fun setPlayImageView()
    fun enablePlayImageView()
    fun setZeroTimer()
}