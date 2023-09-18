package com.practicum.playlistmaker.playlist_details.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.playlist_details.view_model.PlaylistDetailsState
import com.practicum.playlistmaker.playlist_details.view_model.PlaylistDetailsViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistDetailsFragment : Fragment() {

    private var playlistId: Int = 0
    private lateinit var binding: FragmentPlaylistDetailsBinding
    private lateinit var onCLickDebounce: (Track) -> Unit

    private val viewModel: PlaylistDetailsViewModel by viewModel {
        parametersOf(playlistId)
    }
    private val adapter = TrackAdapter { track ->
        onCLickDebounce(track)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistId = requireArguments().getInt(PLAYLIST_ARG)

        onCLickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_playlistDetailsFragment_to_playerFragment,
                PlayerFragment.createArgs(track.toString())
            )
        }

        binding.tracksInPlRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapter
        }

        viewModel.observePlaylistState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistDetailsState.PlaylistScreen -> renderScreen(state)
            }
        }
    }

    private fun renderScreen(state: PlaylistDetailsState.PlaylistScreen) {
        binding.playlistName.text = state.name
        binding.playlistDescription.text = state.description
        binding.playlistDuration.text = state.duration
        binding.numberOfTracks.text = state.numberOfTracks
        setCoverImage(state.coverUri)
        adapter.trackList.clear()
        adapter.trackList.addAll(state.tracks)
        adapter.notifyDataSetChanged()
    }

    private fun setCoverImage(uri: String) {
        Glide.with(binding.playlistCoverImage)
            .load(uri)
            .placeholder(R.drawable.ic_track_placeholder_large)
            .transform(CenterCrop())
            .into(binding.playlistCoverImage)
    }

    companion object {
        fun createArgs(playlistId: Int): Bundle = bundleOf(PLAYLIST_ARG to playlistId)
        private const val PLAYLIST_ARG = "PLAYLIST_ARG"
        const val CLICK_DEBOUNCE_DELAY = 1_000L
    }
}