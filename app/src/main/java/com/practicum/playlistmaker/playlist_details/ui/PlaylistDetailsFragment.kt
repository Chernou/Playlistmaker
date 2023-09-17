package com.practicum.playlistmaker.playlist_details.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.practicum.playlistmaker.playlist_details.view_model.PlaylistDetailsViewModel
import com.practicum.playlistmaker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistDetailsFragment : Fragment() {

    private var playlistId: Int = 0
    private lateinit var binding: FragmentPlaylistDetailsBinding

    private val viewModel: PlaylistDetailsViewModel by viewModel {
        parametersOf(playlistId)
    }
    private val adapter = TrackAdapter {track ->

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
    }
}