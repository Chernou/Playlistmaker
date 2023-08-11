package com.practicum.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.add_to_pl_rv.PlaylistsSmallAdapter
import com.practicum.playlistmaker.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.player.view_model.PlaylistsState
import com.practicum.playlistmaker.player.view_model.ToastState
import com.practicum.playlistmaker.playlists.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.utils.NavigationRouter
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {

    private lateinit var currentPlaybackTime: TextView
    private lateinit var playImageView: ImageView
    private lateinit var track: Track
    private lateinit var favoriteImageView: ImageView
    private lateinit var playlistsRecyclerView: RecyclerView

    private var playlistsAdapter = PlaylistsSmallAdapter {
        //todo implement
    }

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(OPEN_TRACK_INTENT, Track::class.java)
        } else {
            intent.getParcelableExtra(OPEN_TRACK_INTENT)
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
        val addToPlaylist: ImageView = findViewById(R.id.add_to_playlist)
        val createPlaylist: TextView = findViewById(R.id.create_playlist)
        val bottomSheetContainer: ViewGroup = findViewById(R.id.bottom_sheet_container)
        val grayOverlay: View = findViewById(R.id.overlay)

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> grayOverlay.visibility = View.GONE
                    else -> grayOverlay.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        currentPlaybackTime = findViewById(R.id.current_playback_time)
        favoriteImageView = findViewById(R.id.favorite_image)
        playImageView = findViewById(R.id.play_image)
        playlistsRecyclerView = findViewById<RecyclerView>(R.id.playlists_recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playlistsAdapter
        }
        playImageView.isEnabled = false
        playImageView.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.ic_play_button
            )
        )

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

        setFavoriteButton(track.isFavorite)

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

        viewModel.observeState().observe(this) {
            playImageView.isEnabled = it.isPlayButtonEnabled
            currentPlaybackTime.text = it.progress
            setPlayOrPauseImage(it.buttonText)
        }
        viewModel.observeToastState().observe(this) { toastState ->
            if (toastState is ToastState.Show) {
                noPreviewUrlMessage(toastState.additionalMessage)
                viewModel.toastWasShown()
            }
        }
        viewModel.observeIsFavorite().observe(this) { isFavorite ->
            setFavoriteButton(isFavorite)
        }
        viewModel.observePlaylists().observe(this) {
            when (it) {
                is PlaylistsState.DisplayPlaylists -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    displayPlaylists(it.playlists)
                }

                is PlaylistsState.HidePlaylists -> bottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_HIDDEN
            }
        }
        viewModel.preparePlayer()
        playImageView.setOnClickListener {
            viewModel.onPlayPressed()
        }

        val router: NavigationRouter = getKoin().get {
            parametersOf(this)
        }
        toolbar.setNavigationOnClickListener {
            router.goBack()
        }

        favoriteImageView.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        addToPlaylist.setOnClickListener {
            viewModel.addToPlaylistClicked()
        }

        createPlaylist.setOnClickListener {
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun noPreviewUrlMessage(additionalMessage: String) {
        Toast.makeText(this, additionalMessage, Toast.LENGTH_SHORT).show()
    }

    private fun setPlayOrPauseImage(buttonText: String) {
        when (buttonText) {
            PLAY_BUTTON ->
                playImageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.ic_play_button
                    )
                )

            else ->
                playImageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.ic_pause_button
                    )
                )
        }
    }

    private fun setFavoriteButton(isFavorite: Boolean) {
        if (isFavorite) {
            favoriteImageView.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.ic_remove_from_favorites
                )
            )
        } else {
            favoriteImageView.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.ic_add_to_favorites
                )
            )
        }
    }

    private fun displayPlaylists(playlists: List<Playlist>) {
        playlistsAdapter.playlists.clear()
        playlistsAdapter.playlists.addAll(playlists)
        playlistsAdapter.notifyDataSetChanged()
    }

    companion object {
        const val OPEN_TRACK_INTENT = "TRACK INTENT"
        const val PLAY_BUTTON = "PLAY_BUTTON"
    }
}