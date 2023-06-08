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
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeText().observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.empty_media_text).text = it
        }

        view.findViewById<ImageView>(R.id.empty_media_image)
            .setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.nothing_is_found
                )
            )
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }
}