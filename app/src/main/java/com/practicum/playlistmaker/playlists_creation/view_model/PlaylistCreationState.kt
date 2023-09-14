package com.practicum.playlistmaker.playlists_creation.view_model

sealed interface PlaylistCreationState {
    object EmptyState : PlaylistCreationState
    class CreationInProgress(isButtonActive: Boolean) : PlaylistCreationState
}