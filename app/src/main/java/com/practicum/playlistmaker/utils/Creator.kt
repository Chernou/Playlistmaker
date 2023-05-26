package com.practicum.playlistmaker.utils

import android.content.Context
import androidx.activity.ComponentActivity
import com.practicum.playlistmaker.player.data.Player
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerApi
import com.practicum.playlistmaker.player.view_model.api.PlayerInteractorApi
import com.practicum.playlistmaker.search.data.api.ResourceProvider
import com.practicum.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.sharedprefs.LocalStorage
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.data.api.SearchRepository
import com.practicum.playlistmaker.search.data.impl.ResourceProviderImpl
import com.practicum.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.practicum.playlistmaker.settings.data.api.SettingsRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractor
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl

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

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(provideExternalNavigator(context))
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(provideSettingsRepository(context))
    }

    fun provideSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(
            context.getSharedPreferences(
                SHARED_PREFERENCE,
                Context.MODE_PRIVATE
            )
        )
    }

    fun provideResourceProvider(context: Context): ResourceProvider {
        return ResourceProviderImpl(context)
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

    private fun provideExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }
}