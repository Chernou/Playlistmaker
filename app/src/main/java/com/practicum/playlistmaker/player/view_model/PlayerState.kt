package com.practicum.playlistmaker.player.view_model

sealed interface PlayerState {
    object DefaultState: PlayerState
    object PreparedState: PlayerState
    object PlayingState: PlayerState
    object PauseState: PlayerState

}