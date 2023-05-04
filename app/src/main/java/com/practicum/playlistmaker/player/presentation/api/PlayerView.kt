package com.practicum.playlistmaker.player.presentation.api

interface PlayerView {
    fun moveToPreviousScreen()
    fun noPreviewUrlMessage()
    fun setPlaybackTime(time: String)
    fun setPauseImageView()
    fun setPlayImageView()
    fun enablePlayImageView()
    fun setZeroTimer()
}