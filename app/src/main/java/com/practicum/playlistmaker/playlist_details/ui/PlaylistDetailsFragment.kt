package com.practicum.playlistmaker.playlist_details.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.practicum.playlistmaker.playlist_details.view_model.PlaylistDetailsViewModel
import com.practicum.playlistmaker.playlists_creation.domain.model.Playlist
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistDetailsFragment : Fragment() {

    private lateinit var playlist: Playlist
    private lateinit var binding: FragmentPlaylistDetailsBinding

    private val gson: Gson by inject()
    private val viewModel: PlaylistDetailsViewModel by viewModel {
        parametersOf(playlist)
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

        playlist = gson.fromJson(
            requireArguments().getString(PLAYLIST_ARG),
            object : TypeToken<Playlist>() {}.type
        )

        setCoverImage()
        binding.playlistName.text = playlist.name
        if (playlist.description.isNotEmpty()) {
            binding.playlistDescription.text = playlist.description
        } else {
            binding.playlistDescription.visibility = View.GONE
        }
    }

    private fun setCoverImage() {
        Glide.with(binding.playlistCoverImage)
            .load(playlist.coverUri)
            .placeholder(R.drawable.ic_track_placeholder_large)
            .transform(CenterCrop())
            .into(binding.playlistCoverImage)
    }

    companion object {
        fun createArgs(playlistJson: String): Bundle = bundleOf(PLAYLIST_ARG to playlistJson)
        private const val PLAYLIST_ARG = "PLAYLIST_ARG"
    }
}