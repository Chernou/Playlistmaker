package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.data.LocalStorage
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.utils.ResourceProvider
import com.practicum.playlistmaker.search.data.api.SearchRepository
import com.practicum.playlistmaker.search.data.dto.SearchRequest
import com.practicum.playlistmaker.search.data.dto.SearchResponse
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.utils.DateUtils.formatTime
import com.practicum.playlistmaker.utils.DateUtils.getYear
import com.practicum.playlistmaker.utils.Resource
import com.practicum.playlistmaker.utils.TextUtils
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf

class SearchRepositoryImpl(
    private val localStorage: LocalStorage
) : SearchRepository, KoinComponent {

    override fun searchTracks(query: String): Resource<List<Track>> {
        val resourceProvider: ResourceProvider = getKoin().get()
        val networkClient: NetworkClient = getKoin().get()
        val searchRequest: SearchRequest = getKoin().get {
            parametersOf(query)
        }
        val response = networkClient.doRequest(searchRequest)
        return when (response.resultCode) {
            NO_CONNECTIVITY_ERROR -> {
                Resource.Error(resourceProvider.getString(R.string.no_internet_connection))
            }
            SUCCESSFUL_SEARCH_CODE -> {
                Resource.Success((response as SearchResponse).trackList.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.country,
                        it.releaseDate,
                        getYear(it.releaseDate),
                        formatTime(it.duration),
                        it.artworkUri,
                        TextUtils.getHighResArtwork(it.artworkUri),
                        it.genre,
                        it.album,
                        it.previewUrl
                    )
                })
            }
            else -> {
                Resource.Error(resourceProvider.getString(R.string.server_error))
            }
        }
    }

    override fun getSearchHistory(): ArrayList<Track> {
        return localStorage.getSearchHistory()
    }

    override fun clearSearchHistory() {
        localStorage.clearSearchHistory()
    }

    override fun addTrackToSearchHistory(track: Track) {
        localStorage.addTrackToSearchHistory(track)
    }

    companion object {
        const val SUCCESSFUL_SEARCH_CODE = 200
        const val NO_CONNECTIVITY_ERROR = -1
    }
}