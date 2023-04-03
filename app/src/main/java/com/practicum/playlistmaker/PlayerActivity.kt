package com.practicum.playlistmaker

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val backArrowImageView: ImageView = findViewById(R.id.back_arrow)
        val coverImageView: ImageView = findViewById(R.id.cover_image)
        val trackName: TextView = findViewById(R.id.track_name)
        val artistName: TextView = findViewById(R.id.artist_name)
        //val currentPlaybackTime: TextView = findViewById(R.id.current_playback_time)
        val trackDuration: TextView = findViewById(R.id.track_duration)
        val album: TextView = findViewById(R.id.album)
        val trackAlbum: TextView = findViewById(R.id.track_album)
        val trackYear: TextView = findViewById(R.id.track_year)
        val trackGenre: TextView = findViewById(R.id.track_genre)
        val trackCountry: TextView = findViewById(R.id.track_country)
        //val queueImageView: ImageView = findViewById(R.id.queue_image)
        //val playImageView: ImageView = findViewById(R.id.play_image)
        //val likeImageView: ImageView = findViewById(R.id.like_image)

        val track = intent.getParcelableExtra<Track>(Track::class.java.simpleName) as Track

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)
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
            .transform(RoundedCorners(coverImageView.resources
                .getDimensionPixelSize(R.dimen.rounded_corners_album)))
            .placeholder(R.drawable.ic_track_placeholder_small)
            .into(coverImageView)

        backArrowImageView.setOnClickListener {
            onBackPressed()
        }
    }

    private fun removeLastChar(str: String?): String? {
        return str?.replaceFirst(".$".toRegex(), "")
    }

    private fun getCoverArtwork(artworkUriLowRes: String) : String {
        return artworkUriLowRes.replaceAfterLast('/',"512x512bb.jpg")
    }

}