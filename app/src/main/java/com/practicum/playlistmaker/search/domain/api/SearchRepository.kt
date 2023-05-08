package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.utils.Resource

interface SearchRepository {

    fun searchTracks(query: String): Resource<List<Track>>

}