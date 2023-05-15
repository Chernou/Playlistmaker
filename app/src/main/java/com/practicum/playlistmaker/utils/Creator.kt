package com.practicum.playlistmaker.utils

import android.content.Context
import com.practicum.playlistmaker.player.data.Player
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerApi
import com.practicum.playlistmaker.player.view_model.PlayerPresenter
import com.practicum.playlistmaker.player.view_model.api.PlayerInteractorApi
import com.practicum.playlistmaker.player.view_model.api.PlayerView
import com.practicum.playlistmaker.search.data.SearchHistory
import com.practicum.playlistmaker.search.domain.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.practicum.playlistmaker.search.view_model.SearchViewModel
import com.practicum.playlistmaker.search.view_model.SearchRouter
import com.practicum.playlistmaker.search.view_model.api.SearchTracksView

object Creator {

    fun provideSearchPresenter(
        view: SearchTracksView,
        searchHistory: SearchHistory,
        router: SearchRouter,
        context: Context
    ): SearchViewModel {
        return SearchViewModel(
            view,
            searchHistory,
            router,
            context
        )
    }

    fun providePlayerPresenter(view: PlayerView, track: Track): PlayerPresenter {
        return PlayerPresenter(
            view = view,
            track = track,
        )
    }

    fun providePlayerInteractor(): PlayerInteractorApi {
        return PlayerInteractor(providePlayer())
    }

    fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(provideSearchRepository(context))
    }

    private fun provideSearchRepository(context: Context): SearchRepository {
        return SearchRepositoryImpl(provideNetworkClient(context))
    }

    private fun provideNetworkClient(context: Context): RetrofitNetworkClient {
        return RetrofitNetworkClient(context)
    }

    private fun providePlayer(): PlayerApi {
        return Player()
    }
}