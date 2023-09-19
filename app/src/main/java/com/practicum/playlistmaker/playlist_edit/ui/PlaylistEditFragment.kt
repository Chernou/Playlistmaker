package com.practicum.playlistmaker.playlist_edit.ui

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.playlist_details.view_model.PlaylistDetails
import com.practicum.playlistmaker.playlist_edit.view_model.PlaylistEditViewModel
import com.practicum.playlistmaker.playlist_creation.ui.PlaylistCreationFragment
import com.practicum.playlistmaker.playlist_edit.view_model.PlaylistEditStringData
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
            renderPlaylistData(data)
        }

        viewModel.observePlaylistEditStringData().observe(viewLifecycleOwner) { data->
            overrideScreenStrings(data)
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigateUp()
        }

        binding.createPlaylist.setOnClickListener {
            viewModel.onSaveChangesClicked()
            findNavController().navigateUp()
        }
    }

    private fun renderPlaylistData(data: PlaylistDetails) {
        setPlaylistCover(data.coverUri)
        with(binding) {
            playlistName.setText(data.name)
            playlistDescription.setText(data.description)
        }
    }

    private fun overrideScreenStrings(data: PlaylistEditStringData) {
        with(binding) {
            screenName.setText(data.screenDescription)
            createPlaylist.setText(data.saveChangesButtonText)
        }
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