package com.practicum.playlistmaker.player.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.view_model.PlayerPresenter
import com.practicum.playlistmaker.utils.Creator
import com.practicum.playlistmaker.player.view_model.api.PlayerView
import com.practicum.playlistmaker.search.domain.Track

class PlayerActivity : AppCompatActivity(), PlayerView {

    private lateinit var presenter: PlayerPresenter
    private lateinit var currentPlaybackTime: TextView
    private lateinit var playImageView: ImageView
    private lateinit var track: Track
    //private lateinit var queueImageView: ImageView
    //private lateinit var likeImageView: ImageView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val backArrowImageView: ImageView = findViewById(R.id.back_arrow)
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
        //val queueImageView: ImageView = findViewById(R.id.queue_image)
        //val likeImageView: ImageView = findViewById(R.id.like_image)

        track = intent.getParcelableExtra<Track>(Track::class.java.simpleName) as Track

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

        presenter = Creator.providePlayerPresenter(this, track)

        backArrowImageView.setOnClickListener {
            presenter.backArrowPressed()
        }

        playImageView.setOnClickListener {
            presenter.onPlayPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        presenter.onPaused()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroyed()
    }

    override fun moveToPreviousScreen() {
        onBackPressedDispatcher.onBackPressed()
    }

    override fun setPlaybackTime(time: String) {
        currentPlaybackTime.text = time
    }

    override fun noPreviewUrlMessage() {
        Toast.makeText(this, getText(R.string.no_preview_url), Toast.LENGTH_SHORT).show()
    }

    override fun setPauseImageView() {
        playImageView.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.ic_pause_button
            )
        )
    }

    override fun setPlayImageView() {
        playImageView.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.ic_play_button
            )
        )
    }

    override fun enablePlayImageView() {
        playImageView.isEnabled = true
    }

    override fun setZeroTimer() {
        currentPlaybackTime.text = ZERO_TIMER
    }

    companion object {
        const val ZERO_TIMER = "00:00"
    }
}