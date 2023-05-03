package com.practicum.playlistmaker.player.presentation

interface PlayerView {
    fun moveToPreviousScreen()
    fun noPreviewUrlMessage()
    fun setPlaybackTime(time: String)
    fun setPauseImageView()
    fun setPlayImageView()
    fun enablePlayImageView()
    fun setZeroTimer()
}