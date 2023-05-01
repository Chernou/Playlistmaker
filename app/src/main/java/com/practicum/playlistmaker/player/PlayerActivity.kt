package com.practicum.playlistmaker.player

import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.Track
import com.practicum.playlistmaker.utils.DateUtils.formatTime
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private lateinit var currentPlaybackTime: TextView
    private lateinit var playImageView: ImageView
    private lateinit var mainThreadHandler: Handler
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
        mainThreadHandler = Handler(Looper.getMainLooper())

        track = intent.getParcelableExtra<Track>(Track::class.java.simpleName) as Track

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackDuration.text = formatTime(track.trackTime)
        trackGenre.text = track.genre
        trackCountry.text = track.country

        if (track.album == "") {
            trackAlbum.visibility = View.GONE
            album.visibility = View.GONE
        } else {
            trackAlbum.text = track.album
        }

        val year = LocalDateTime.parse(removeLastChar(track.releaseDate)).year.toString()
        trackYear.text = year

        val artworkUriHighRes = getCoverArtwork(track.artworkUri)
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

        backArrowImageView.setOnClickListener {
            onBackPressed()
        }

        playImageView.setOnClickListener {
            if (track.previewUrl == null) {
                noPreviewUrlMessage()
            } else {
                playbackControl()
            }
        }

        if (track.previewUrl != null) {
            preparePlayer()
        } else {
            noPreviewUrlMessage()
            playImageView.isEnabled = true
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mainThreadHandler.removeCallbacks(setPlaybackTimer())
    }

    private fun preparePlayer() {
        val url = track.previewUrl
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playImageView.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            mainThreadHandler.removeCallbacks(setPlaybackTimer())
            currentPlaybackTime.text = ZERO_TIMER
            switchPlayPauseImage(R.drawable.ic_play_button)
        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        switchPlayPauseImage(R.drawable.ic_pause_button)
        mainThreadHandler.post(setPlaybackTimer())
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        switchPlayPauseImage(R.drawable.ic_play_button)
        mainThreadHandler.removeCallbacks(setPlaybackTimer())
    }

    private fun setPlaybackTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    currentPlaybackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                        .format(mediaPlayer.currentPosition)
                    mainThreadHandler.postDelayed(this, PLAYBACK_TIME_REFRESH)
                }
            }
        }
    }

    private fun noPreviewUrlMessage() {
        Toast.makeText(this, getText(R.string.no_preview_url), Toast.LENGTH_SHORT).show()
    }

    private fun removeLastChar(str: String?): String? {
        return str?.replaceFirst(".$".toRegex(), "")
    }

    private fun getCoverArtwork(artworkUriLowRes: String): String {
        return artworkUriLowRes.replaceAfterLast('/', "512x512bb.jpg")
    }

    private fun switchPlayPauseImage(res: Int) {
        playImageView.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                res
            )
        )
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val PLAYBACK_TIME_REFRESH = 500L
        const val ZERO_TIMER = "00:00"
    }

}