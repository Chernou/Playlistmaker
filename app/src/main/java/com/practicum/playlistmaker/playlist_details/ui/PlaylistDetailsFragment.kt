package com.practicum.playlistmaker.playlist_details.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.playlist_details.view_model.EmptyPlaylistToastState
import com.practicum.playlistmaker.playlist_details.view_model.PlaylistDetails
import com.practicum.playlistmaker.playlist_details.view_model.PlaylistDetailsViewModel
import com.practicum.playlistmaker.playlist_details.view_model.PlaylistMenuState
import com.practicum.playlistmaker.playlist_details.view_model.TracksInPlaylistData
import com.practicum.playlistmaker.search.domain.model.Track
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

    private lateinit var deleteTrackDialog: MaterialAlertDialogBuilder

    private val trackAdapter =
        TrackInPlaylistAdapter(object : TrackInPlaylistAdapter.TrackClickListener {
            override fun onTrackLongClickListener(track: Track): Boolean {
                viewModel.onTrackLongClicked(track)
                deleteTrackDialog.show()
                return true
            }

            override fun onTrackClickListener(track: Track) {
                onCLickDebounce(track)
            }
        })

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

        deleteTrackDialog =
            MaterialAlertDialogBuilder(requireContext(), R.style.AppTheme_MyMaterialAlertDialog)
                .setTitle(resources.getString(R.string.delete_track_title))
                .setMessage(resources.getString(R.string.delete_track_message))
                .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
                }.setPositiveButton(resources.getString(R.string.delete)) { _, _ ->
                    viewModel.onTrackDeleteConfirmed()
                }

        val deletePlaylistDialog =
            MaterialAlertDialogBuilder(requireContext(), R.style.AppTheme_MyMaterialAlertDialog)
                .setTitle(resources.getString(R.string.delete_playlist))
                .setMessage(getDialogueTitle())
                .setNegativeButton(resources.getString(R.string.no)) { _, _ ->
                }.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    viewModel.onDeletePlaylistConfirmed()
                }

        onCLickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_playlistDetailsFragment_to_playerFragment,
                PlayerFragment.createArgs(track.toString())
            )
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.playlistDetailsOverlay.visibility = when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> View.GONE
                    else -> View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        viewModel.observePlaylistData().observe(viewLifecycleOwner) { data ->
            renderPlaylistData(data)
        }

        viewModel.observeTracksLiveDate().observe(viewLifecycleOwner) { tracks ->
            renderTrackData(tracks)
        }

        viewModel.observeToastLiveData().observe(viewLifecycleOwner) { state ->
            if (state == EmptyPlaylistToastState.SHOW) {
                showEmptyPlaylistToast()
                viewModel.toastWasShown()
            }
        }

        viewModel.observePlaylistMenuState().observe(viewLifecycleOwner) { state ->
            if (state == PlaylistMenuState.SHOW) bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HALF_EXPANDED
            viewModel.menuWasShown()
        }

        binding.playlistToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.tracksInPlRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = trackAdapter
        }

        binding.sharePlaylistImage.setOnClickListener {
            viewModel.onShareClicked()
        }

        binding.menuImage.setOnClickListener {
            viewModel.onMenuClicked()
        }

        binding.shareMenuItem.setOnClickListener {
            viewModel.onShareClicked()
        }

        binding.editInfo.setOnClickListener {
            viewModel.onEditInfoClicked()
        }

        binding.deletePlaylist.setOnClickListener {
            deletePlaylistDialog.show()
        }
    }

    private fun renderPlaylistData(playlistData: PlaylistDetails) {
        binding.playlistName.text = playlistData.name
        binding.playlistDescription.text = playlistData.description
        setCoverImage(playlistData.coverUri)
        binding.playlistNameSmall.text = playlistData.name

    }

    private fun renderTrackData(tracksData: TracksInPlaylistData) {
        binding.playlistDuration.text = tracksData.duration
        binding.numberOfTracks.text = tracksData.numberOfTracks
        trackAdapter.trackList.clear()
        trackAdapter.trackList.addAll(tracksData.tracks)
        trackAdapter.notifyDataSetChanged()
        binding.numberOfTracksSmall.text = tracksData.numberOfTracks
    }

    private fun setCoverImage(uri: String) {
        Glide.with(binding.playlistCoverImage)
            .load(uri)
            .placeholder(R.drawable.ic_track_placeholder_large)
            .transform(CenterCrop())
            .into(binding.playlistCoverImage)

        Glide.with(binding.playlistCoverSmall)
            .load(uri)
            .placeholder(R.drawable.ic_track_placeholder_small)
            .transform(CenterCrop())
            .into(binding.playlistCoverSmall)
    }

    private fun showEmptyPlaylistToast() {
        Toast.makeText(
            context,
            resources.getString(R.string.no_tracks_to_share),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun getDialogueTitle(): String {
        return "${resources.getString(R.string.delete_playlist_question)} \"${binding.playlistName.text}\"?"
    }

    companion object {
        fun createArgs(playlistId: Int): Bundle = bundleOf(PLAYLIST_ARG to playlistId)
        private const val PLAYLIST_ARG = "PLAYLIST_ARG"
        const val CLICK_DEBOUNCE_DELAY = 1_000L
    }
}