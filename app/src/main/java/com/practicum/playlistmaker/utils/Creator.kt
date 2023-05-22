package com.practicum.playlistmaker.utils

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import com.practicum.playlistmaker.player.data.Player
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerApi
import com.practicum.playlistmaker.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.player.view_model.api.PlayerInteractorApi
import com.practicum.playlistmaker.search.domain.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.sharedprefs.LocalStorage
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.impl.SearchInteractorImpl

object Creator {

    private const val SHARED_PREFERENCE = "SHARED_PREFERENCE"

    fun providePlayerInteractor(): PlayerInteractorApi {
        return PlayerInteractor(providePlayer())
    }

    fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(provideSearchRepository(context))
    }

    fun provideNavigationRouter(activity: ComponentActivity): NavigationRouter {
        return NavigationRouter(activity)
    }

    private fun provideSearchRepository(context: Context): SearchRepository {
        return SearchRepositoryImpl(
            provideNetworkClient(context),
            LocalStorage(context.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE))
        )
    }

    private fun provideNetworkClient(context: Context): RetrofitNetworkClient {
        return RetrofitNetworkClient(context)
    }

    private fun providePlayer(): PlayerApi {
        return Player()
    }
}