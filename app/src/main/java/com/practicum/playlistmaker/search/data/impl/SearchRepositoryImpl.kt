package com.practicum.playlistmaker.search.data.impl

import android.os.Build
import androidx.annotation.RequiresApi
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.SearchRequest
import com.practicum.playlistmaker.search.data.dto.SearchResponse
import com.practicum.playlistmaker.search.data.sharedprefs.LocalStorage
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.data.api.SearchRepository
import com.practicum.playlistmaker.utils.DateUtils.formatTime
import com.practicum.playlistmaker.utils.DateUtils.getYear
import com.practicum.playlistmaker.utils.Resource
import com.practicum.playlistmaker.utils.TextUtils

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val localStorage: LocalStorage
) : SearchRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun searchTracks(query: String): Resource<List<Track>> {
        val response = networkClient.doRequest(SearchRequest(query))
        return when (response.resultCode) {
            NO_CONNECTIVITY_ERROR -> {
                Resource.Error(NO_CONNECTIVITY_MESSAGE)
            }
            SUCCESSFUL_SEARCH_CODE -> {
                Resource.Success((response as SearchResponse).trackList.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.country,
                        it.releaseDate,
                        getYear(TextUtils.removeLastChar(it.releaseDate)),
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
                Resource.Error(SERVER_ERROR_MESSAGE)
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
        const val NO_CONNECTIVITY_MESSAGE =
            "Проблемы со связью\n\nЗагрузка не удалась. Проверьте подключение к интернету"
        const val SERVER_ERROR_MESSAGE = "Ошибка сервера"
    }
}