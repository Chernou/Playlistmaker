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

class FavoritesFragment : Fragment() {

    private val resourceProvider: ResourceProvider by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.empty_media_image)
            .setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.nothing_is_found
                )
            )

        view.findViewById<TextView>(R.id.empty_media_text).text =
            resourceProvider.getString(R.string.empty_media)
    }
}