package com.practicum.playlistmaker.utils

import android.content.Intent
import androidx.activity.ComponentActivity
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.Track
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf

class NavigationRouter(
    private val activity: ComponentActivity
) : KoinComponent {

    fun goBack() {
        activity.onBackPressedDispatcher.onBackPressed()
    }

    fun openTrack(name: String, track: Track) {
        val playerIntent: Intent = getKoin().get {
            parametersOf(activity, PlayerActivity::class.java)
        }
        playerIntent.putExtra(name, track)
        activity.startActivity(playerIntent)
    }
}