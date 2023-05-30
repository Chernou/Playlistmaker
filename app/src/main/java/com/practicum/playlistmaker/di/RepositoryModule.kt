package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.search.data.api.SearchRepository
import com.practicum.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.settings.data.api.SettingsRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    single<SearchRepository> {
        SearchRepositoryImpl(get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }
}