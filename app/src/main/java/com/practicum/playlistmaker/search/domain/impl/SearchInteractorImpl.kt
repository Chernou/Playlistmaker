package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.utils.Resource
import java.util.concurrent.Executors

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(query: String, consumer: SearchInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.searchTracks(query)) {
                is Resource.Success -> { consumer.consume(resource.data, resource.message) }
                is Resource.Error -> { consumer.consume(null, resource.message) }
            }
        }
    }

    override fun getSearchHistory(): ArrayList<Track> {
        return repository.getSearchHistory()
    }

    override fun addTrackToSearchHistory(track: Track) {
        repository.addTrackToSearchHistory(track)
    }

    override fun clearSearchHistory() {
        repository.clearSearchHistory()
    }
}