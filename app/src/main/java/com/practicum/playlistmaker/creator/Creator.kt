package com.practicum.playlistmaker.creator

import com.practicum.playlistmaker.player.data.Player
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerApi
import com.practicum.playlistmaker.player.presentation.PlayerPresenter
import com.practicum.playlistmaker.player.presentation.api.PlayerInteractorApi
import com.practicum.playlistmaker.player.presentation.api.PlayerView
import com.practicum.playlistmaker.search.domain.Track

object Creator {

    fun providePlayerPresenter(view: PlayerView, track: Track, ): PlayerPresenter {
        return PlayerPresenter(
            view = view,
            track = track,
        )
    }

    fun getPlayerInteractor(): PlayerInteractorApi {
        return PlayerInteractor(getPlayer())
    }

    private fun getPlayer(): PlayerApi {
        return Player()
    }
}
