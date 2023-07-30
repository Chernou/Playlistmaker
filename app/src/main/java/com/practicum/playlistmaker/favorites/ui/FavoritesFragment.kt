package com.practicum.playlistmaker.favorites.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.favorites.view_model.FavoritesState
import com.practicum.playlistmaker.favorites.view_model.FavoritesViewModel
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.utils.NavigationRouter
import com.practicum.playlistmaker.utils.debounce
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModel()
    private lateinit var favoritesRecyclerView: RecyclerView
    private lateinit var emptyFavoritesLayout: ViewGroup
    private val favoritesAdapter = TrackAdapter {
        onCLickDebounce(it)
    }

    private val router: NavigationRouter by inject {
        parametersOf(requireActivity())
    }

    private lateinit var onCLickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesRecyclerView = view.findViewById(R.id.favorites_recycler_view)
        favoritesRecyclerView.apply {
            adapter = favoritesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        emptyFavoritesLayout = view.findViewById(R.id.empty_favorites_layout)

        viewModel.observeFavoritesState().observe(viewLifecycleOwner) {
            render(it)
        }

        onCLickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            router.openTrack(OPEN_TRACK_INTENT, track)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.displayState()
    }

    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.DisplayFavorites -> displayFavorites(state.tracks)
            is FavoritesState.EmptyFavorites -> displayPlaceholer()
        }
    }

    private fun displayFavorites(tracks: List<Track>) {
        emptyFavoritesLayout.visibility = View.GONE
        favoritesRecyclerView.visibility = View.VISIBLE
        favoritesAdapter.trackList.clear()
        favoritesAdapter.trackList.addAll(tracks)
        favoritesAdapter.notifyDataSetChanged()
    }

    private fun displayPlaceholer() {
        emptyFavoritesLayout.visibility = View.VISIBLE
        favoritesRecyclerView.visibility = View.GONE
    }

    companion object {
        fun newInstance() = FavoritesFragment()
        const val CLICK_DEBOUNCE_DELAY = 1_000L
        const val OPEN_TRACK_INTENT = "TRACK INTENT"
    }
}