package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.favorites.view_model.FavoritesViewModel
import com.practicum.playlistmaker.playlists.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.playlist_details.view_model.PlaylistDetailsViewModel
import com.practicum.playlistmaker.playlist_edit.view_model.PlaylistEditViewModel
import com.practicum.playlistmaker.playlist_creation.view_model.PlaylistCreationViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel { (track: Track) ->
        PlayerViewModel(track, get(), get(), get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        PlaylistCreationViewModel(get(), get(), get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel { (playlistId: Int) ->
        PlaylistDetailsViewModel(playlistId, get(), get())
    }

    viewModel { (playlistId: Int) ->
        PlaylistEditViewModel(get(), get(), get(), playlistId)
    }
}