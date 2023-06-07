package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.PlayerInteractorImpl
import com.practicum.playlistmaker.player.view_model.api.PlayerInteractor
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    single<SearchInteractor> {
        SearchInteractorImpl(get(), get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }
}