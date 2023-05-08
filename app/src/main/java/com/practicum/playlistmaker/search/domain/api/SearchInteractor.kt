package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.Track

interface SearchInteractor {
    fun searchTracks(query: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>)
    }
}