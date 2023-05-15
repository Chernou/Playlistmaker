package com.practicum.playlistmaker.search.view_model

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.player.ui.PlayerActivity

class SearchRouter(
    private val activity: AppCompatActivity
) {

    fun openTrack(track: Track) {
        val playerIntent = Intent(activity, PlayerActivity::class.java)
        playerIntent.putExtra(Track::class.java.simpleName, track)
        activity.startActivity(playerIntent)
    }

    fun goBack() {
        activity.finish()
    }
}