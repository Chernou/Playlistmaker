package com.practicum.playlistmaker.search.view_model

import com.practicum.playlistmaker.search.domain.Track

sealed interface SearchState {

    object Loading : SearchState
    data class SearchContent(val tracks: List<Track>) : SearchState
    data class HistoryContent(val tracks: List<Track>) : SearchState //todo move history to presenter
    data class Error(val errorMessage: String) : SearchState
    object Empty : SearchState

}