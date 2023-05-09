package com.practicum.playlistmaker.utils

import android.content.Context
import com.practicum.playlistmaker.player.data.Player
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerApi
import com.practicum.playlistmaker.player.presentation.PlayerPresenter
import com.practicum.playlistmaker.player.presentation.api.PlayerInteractorApi
import com.practicum.playlistmaker.player.presentation.api.PlayerView
import com.practicum.playlistmaker.search.data.SearchHistory
import com.practicum.playlistmaker.search.domain.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.practicum.playlistmaker.search.presentation.SearchPresenter
import com.practicum.playlistmaker.search.presentation.SearchRouter
import com.practicum.playlistmaker.search.presentation.api.SearchTracksView

object Creator {

    fun provideSearchPresenter(
        view: SearchTracksView,
        searchHistory: SearchHistory,
        router: SearchRouter,
        context: Context
    ): SearchPresenter {
        return SearchPresenter(
            view,
            searchHistory,
            router,
            provideSearchInteractor(context)
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

    private fun provideSearchInteractor(context: Context): SearchInteractor {
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