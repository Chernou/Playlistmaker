package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.view_model.FavoritesViewModel
import com.practicum.playlistmaker.media.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {(track: Track) ->
        PlayerViewModel(track ,get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoritesViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }

}