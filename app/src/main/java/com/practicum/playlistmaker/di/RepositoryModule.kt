package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.favorites.data.FavoritesRepositoryImpl
import com.practicum.playlistmaker.favorites.data.converters.TrackDbConverter
import com.practicum.playlistmaker.favorites.domain.api.FavoritesRepository
import com.practicum.playlistmaker.playlist_details.data.PlaylistRepositoryImpl
import com.practicum.playlistmaker.playlist_details.domain.api.PlaylistRepository
import com.practicum.playlistmaker.playlists_creation.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.playlists_creation.data.db.PlaylistsDbRepositoryImpl
import com.practicum.playlistmaker.playlists_creation.data.local_files.PlaylistsFilesRepositoryImpl
import com.practicum.playlistmaker.playlists_creation.domain.api.db.PlaylistsDbRepository
import com.practicum.playlistmaker.playlists_creation.domain.api.local_files.PlaylistsFilesRepository
import com.practicum.playlistmaker.search.data.api.SearchRepository
import com.practicum.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.settings.data.api.SettingsRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    single<SearchRepository> {
        SearchRepositoryImpl(get(), get(), get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single { TrackDbConverter() }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    single { PlaylistDbConverter() }

    single<PlaylistsDbRepository> {
        PlaylistsDbRepositoryImpl(get(), get(), get())
    }

    single<PlaylistsFilesRepository> {
        PlaylistsFilesRepositoryImpl(get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get())
    }
}