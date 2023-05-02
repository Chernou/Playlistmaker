package com.practicum.playlistmaker.player.presentation

class PlayerPresenter(
    private val view: PlayerView
) {
    fun backArrowPressed() {
        view.moveToPreviousScreen()
    }

}