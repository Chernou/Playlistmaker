package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.favorites.view_model.PlaylistsState
import com.practicum.playlistmaker.media.ui.grid_layout.PlaylistsAdapter
import com.practicum.playlistmaker.media.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.playlists.domain.model.Playlist
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private lateinit var emptyPlaylistsLayout: ViewGroup
    private lateinit var recyclerView: RecyclerView
    private val viewModel: PlaylistsViewModel by viewModel()
    private val playlistsAdapter = PlaylistsAdapter {
        onClickDebounce(it)
    }
    private lateinit var onClickDebounce: (Playlist) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emptyPlaylistsLayout = view.findViewById(R.id.empty_playlists_layout)
        recyclerView = view.findViewById<RecyclerView>(R.id.playlists_recycler_view).apply {
            layoutManager = GridLayoutManager(requireContext(), NUMBER_OF_COLUMNS)
            adapter = playlistsAdapter
        }

        viewModel.displayState()
        viewModel.observePlaylists().observe(viewLifecycleOwner) {
            render(it)
        }

        view.findViewById<MaterialButton>(R.id.new_playlist_button).setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_playlistCreationFragment)
        }

        onClickDebounce = debounce<Playlist>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->
            //todo implement onClick
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.displayState()
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.DisplayEmptyPlaylists -> displayEmptyPlaylists()
            is PlaylistsState.DisplayPlaylists -> displayPlaylists(state.playlists)
        }

    }

    private fun displayEmptyPlaylists() {
        recyclerView.visibility = View.GONE
        emptyPlaylistsLayout.visibility = View.VISIBLE
    }

    private fun displayPlaylists(playlists: List<Playlist>) {
        emptyPlaylistsLayout.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        playlistsAdapter.playlists.clear()
        playlistsAdapter.playlists.addAll(playlists)
        playlistsAdapter.notifyDataSetChanged()
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
        private const val NUMBER_OF_COLUMNS = 2
        private const val CLICK_DEBOUNCE_DELAY = 1_000L
    }
}