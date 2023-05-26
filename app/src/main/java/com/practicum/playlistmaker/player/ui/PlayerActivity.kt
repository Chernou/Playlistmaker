package com.practicum.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.view_model.PlayerState
import com.practicum.playlistmaker.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.player.view_model.ToastState
import com.practicum.playlistmaker.utils.Creator
import com.practicum.playlistmaker.search.domain.Track

class PlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var currentPlaybackTime: TextView
    private lateinit var playImageView: ImageView
    private lateinit var track: Track
    private val router = Creator.provideNavigationRouter(this)
    //private lateinit var queueImageView: ImageView
    //private lateinit var likeImageView: ImageView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Track::class.java.simpleName, Track::class.java)
        } else {
            intent.getParcelableExtra(Track::class.java.simpleName)
        } as Track
        val toolbar = findViewById<Toolbar>(R.id.player_toolbar)
        val coverImageView: ImageView = findViewById(R.id.cover_image)
        val trackName: TextView = findViewById(R.id.track_name)
        val artistName: TextView = findViewById(R.id.artist_name)
        val trackDuration: TextView = findViewById(R.id.track_duration)
        val album: TextView = findViewById(R.id.album)
        val trackAlbum: TextView = findViewById(R.id.track_album)
        val trackYear: TextView = findViewById(R.id.track_year)
        val trackGenre: TextView = findViewById(R.id.track_genre)
        val trackCountry: TextView = findViewById(R.id.track_country)
        currentPlaybackTime = findViewById(R.id.current_playback_time)
        playImageView = findViewById(R.id.play_image)
        playImageView.isEnabled = false
        playImageView.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.ic_play_button
            )
        )
        //val queueImageView: ImageView = findViewById(R.id.queue_image)
        //val likeImageView: ImageView = findViewById(R.id.like_image)

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackDuration.text = track.duration
        trackGenre.text = track.genre
        trackCountry.text = track.country
        trackYear.text = track.releaseYear

        if (track.album == "") {
            trackAlbum.visibility = View.GONE
            album.visibility = View.GONE
        } else {
            trackAlbum.text = track.album
        }

        val artworkUriHighRes = track.highResArtworkUri
        Glide.with(coverImageView)
            .load(artworkUriHighRes)
            .centerCrop()
            .transform(
                RoundedCorners(
                    coverImageView.resources
                        .getDimensionPixelSize(R.dimen.rounded_corners_album)
                )
            )
            .placeholder(R.drawable.ic_track_placeholder_small)
            .into(coverImageView)

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getViewModelFactory(track, this)
        )[PlayerViewModel::class.java]
        viewModel.observeState().observe(this) {
            render(it)
        }
        viewModel.observeToastState().observe(this) { toastState ->
            if (toastState is ToastState.Show) {
                noPreviewUrlMessage(toastState.additionalMessage)
                viewModel.toastWasShown()
            }
        }
        viewModel.observePlaybackTime().observe(this) { playbackTime ->
            setPlaybackTime(playbackTime)
        }
        viewModel.preparePlayer()
        playImageView.setOnClickListener {
            viewModel.onPlayPressed()
        }
        toolbar.setNavigationOnClickListener {
            router.goBack()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPaused()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onCleared()
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.DefaultState -> setDefaultScreen()
            is PlayerState.PreparedState -> setPreparedScreen()
            is PlayerState.PlayingState -> setPlayingScreen()
            is PlayerState.PauseState -> setPauseScreen()
        }
    }

    private fun setDefaultScreen() {
        playImageView.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.ic_play_button
            )
        )
        playImageView.isEnabled = false
    }

    private fun setPreparedScreen() {
        playImageView.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.ic_play_button
            )
        )
        playImageView.isEnabled = true
        currentPlaybackTime.text = ZERO_TIMER
    }

    private fun setPlayingScreen() {
        playImageView.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.ic_pause_button
            )
        )
        playImageView.isEnabled = true
    }

    private fun setPauseScreen() {
        playImageView.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.ic_play_button
            )
        )
        playImageView.isEnabled = true
    }

    private fun setPlaybackTime(time: String) {
        currentPlaybackTime.text = time
    }

    private fun noPreviewUrlMessage(additionalMessage: String) {
        Toast.makeText(this, additionalMessage, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val ZERO_TIMER = "00:00"
    }
}