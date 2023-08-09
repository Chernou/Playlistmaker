package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.practicum.playlistmaker.R

class PlaylistsFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.no_playlists_image)
        textView = view.findViewById(R.id.no_playlists_text)

        imageView.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.nothing_is_found
            )
        )

        view.findViewById<MaterialButton>(R.id.new_playlist_button).setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_playlistCreationFragment)
        }
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}