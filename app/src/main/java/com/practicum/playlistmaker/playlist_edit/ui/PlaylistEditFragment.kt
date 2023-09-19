package com.practicum.playlistmaker.playlist_edit.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.playlist_details.view_model.PlaylistDetails
import com.practicum.playlistmaker.playlist_edit.view_model.PlaylistEditViewModel
import com.practicum.playlistmaker.playlist_creation.ui.PlaylistCreationFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistEditFragment : PlaylistCreationFragment() {

    private var playlistId = 0
    override val viewModel: PlaylistEditViewModel by viewModel {
        parametersOf(playlistId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playlistId = requireArguments().getInt(PLAYLIST_ARG)
        super.onViewCreated(view, savedInstanceState)

        viewModel.observePlaylistData().observe(viewLifecycleOwner) { data ->
            renderScreen(data)
        }
    }

    private fun renderScreen(data: PlaylistDetails) {
        setPlaylistCover(data.coverUri)
        binding.playlistName.setText(data.name)
        binding.playlistDescription.setText(data.description)
    }

    private fun setPlaylistCover(coverUri: String) {
        Glide.with(binding.plCover)
            .load(coverUri)
            .centerCrop()
            .into(binding.plCover)
    }

    companion object {
        fun createArgs(playlistId: Int): Bundle = bundleOf(PLAYLIST_ARG to playlistId)
        private const val PLAYLIST_ARG = "PLAYLIST_ARG"
    }
}