package com.practicum.playlistmaker.search.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.practicum.playlistmaker.search.data.dto.SearchRequest
import com.practicum.playlistmaker.search.data.dto.SearchResponse
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.utils.DateUtils.formatTime
import com.practicum.playlistmaker.utils.DateUtils.getYear
import com.practicum.playlistmaker.utils.TextUtils

class SearchRepositoryImpl(
    private val networkClient: NetworkClient
) : SearchRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun searchTracks(query: String): List<Track> {
        val response = networkClient.doRequest(SearchRequest(query))
        return if (response.resultCode == SUCCESSFUL_SEARCH_CODE) {
            (response as SearchResponse).trackList.map {
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
            }
        } else {
            emptyList()
        }
    }

    companion object {
        const val SUCCESSFUL_SEARCH_CODE = 200
    }
}