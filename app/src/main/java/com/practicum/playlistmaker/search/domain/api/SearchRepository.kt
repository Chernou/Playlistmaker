package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.Track

interface SearchRepository {

    fun searchTracks(query: String): List<Track>

}