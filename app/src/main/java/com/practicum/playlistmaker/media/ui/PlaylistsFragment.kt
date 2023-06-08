package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.view_model.FavoritesViewModel
import com.practicum.playlistmaker.media.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.utils.ResourceProvider
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class PlaylistsFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeText().observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.no_playlists_text).text = it
        }

        view.findViewById<ImageView>(R.id.no_playlists_image)
            .setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.nothing_is_found
                )
            )
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}