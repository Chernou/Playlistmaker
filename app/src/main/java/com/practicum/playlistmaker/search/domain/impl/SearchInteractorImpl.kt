package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.data.api.SearchRepository
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.utils.Resource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.ExecutorService

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor, KoinComponent {

    private val executor: ExecutorService by inject()

    override fun searchTracks(query: String, consumer: SearchInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.searchTracks(query)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, resource.message)
                }
                is Resource.Error -> {
                    consumer.consume(null, resource.message)
                }
            }
        }
    }

    override fun getSearchHistory() = repository.getSearchHistory()

    override fun addTrackToSearchHistory(track: Track) {
        repository.addTrackToSearchHistory(track)
    }

    override fun clearSearchHistory() {
        repository.clearSearchHistory()
    }
}