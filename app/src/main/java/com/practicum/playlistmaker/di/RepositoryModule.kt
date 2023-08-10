package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.favorites.data.FavoritesRepositoryImpl
import com.practicum.playlistmaker.favorites.data.converters.TrackDbConverter
import com.practicum.playlistmaker.favorites.domain.api.FavoritesRepository
import com.practicum.playlistmaker.playlists.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.playlists.data.db.PlaylistsDbRepositoryImpl
import com.practicum.playlistmaker.playlists.data.local_files.PlaylistsFilesRepositoryImpl
import com.practicum.playlistmaker.playlists.domain.api.db.PlaylistsDbRepository
import com.practicum.playlistmaker.playlists.domain.api.local_files.PlaylistsFilesRepository
import com.practicum.playlistmaker.search.data.api.SearchRepository
import com.practicum.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.settings.data.api.SettingsRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    single<SearchRepository> {
        SearchRepositoryImpl(get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    factory { TrackDbConverter() }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    factory { PlaylistDbConverter(get()) }

    single<PlaylistsDbRepository> {
        PlaylistsDbRepositoryImpl(get(), get())
    }

    single<PlaylistsFilesRepository> {
        PlaylistsFilesRepositoryImpl(get())
    }
}