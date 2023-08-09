package com.practicum.playlistmaker.playlists.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlists.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistCreationFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var coverImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist_creation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        val createPlTextView = view.findViewById<TextView>(R.id.create_playlist)
        nameEditText = view.findViewById(R.id.playlist_name)
        descriptionEditText = view.findViewById(R.id.playlist_description)
        coverImageView = view.findViewById(R.id.pl_cover)

        toolbar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Glide.with(coverImageView)
                        .load(uri.toString())
                        .centerCrop()
                        .into(coverImageView)
                    viewModel.coverIsChosen(uri.toString())
                }
            }

        createPlTextView.setOnClickListener {
            viewModel.onCreatePlClicked()

        }

        coverImageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }
}