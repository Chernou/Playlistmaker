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
import com.practicum.playlistmaker.utils.ResourceProvider
import org.koin.android.ext.android.inject

class PlaylistsFragment : Fragment() {

    private val resourceProvider: ResourceProvider by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.no_playlists_image)
            .setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.nothing_is_found
                )
            )

        view.findViewById<TextView>(R.id.no_playlists_text).text =
            resourceProvider.getString(R.string.no_playlists)
    }
}