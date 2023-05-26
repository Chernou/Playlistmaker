package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.data.PlayerImpl
import com.practicum.playlistmaker.player.domain.api.Player
import com.practicum.playlistmaker.search.data.LocalStorage
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.sharedprefs.LocalStorageImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    factory<Player> {
        PlayerImpl()
    }

    single<LocalStorage> {
        LocalStorageImpl(androidContext())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(androidContext())
    }

}