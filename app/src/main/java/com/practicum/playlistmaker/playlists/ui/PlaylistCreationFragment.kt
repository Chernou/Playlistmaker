package com.practicum.playlistmaker.playlists.ui

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlists.view_model.PlaylistsCreationViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistCreationFragment : Fragment() {

    private val viewModel: PlaylistsCreationViewModel by viewModel()
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var coverImageView: ImageView
    private lateinit var createPlTextView: TextView
    private lateinit var nameHint: TextView
    private lateinit var descriptionHint: TextView

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
        createPlTextView = view.findViewById(R.id.create_playlist)
        nameEditText = view.findViewById(R.id.playlist_name)
        descriptionEditText = view.findViewById(R.id.playlist_description)
        coverImageView = view.findViewById(R.id.pl_cover)
        nameHint = view.findViewById(R.id.playlist_name_hint)
        descriptionHint = view.findViewById(R.id.playlist_description_hint)

        var coverPickedFlag = false
        var pickedImageUri: Uri? = null
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    pickedImageUri = uri
                    Glide.with(coverImageView)
                        .load(uri.toString())
                        .centerCrop()
                        .into(coverImageView)
                    coverPickedFlag = true
                }
            }

        createPlTextView.setOnClickListener {
            viewModel.onCreatePlClicked(
                nameEditText.text.toString(),
                descriptionEditText.text.toString(),
                pickedImageUri
            )
            findNavController().navigateUp()
        }

        coverImageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        nameEditText.addTextChangedListener(plNameTextWatcher)
        descriptionEditText.addTextChangedListener(plDescriptionTextWatcher)

        val confirmDialog =
            MaterialAlertDialogBuilder(requireContext(), R.style.AppTheme_MyMaterialAlertDialog)
                .setTitle(resources.getString(R.string.save_pl_dialog_title))
                .setMessage(resources.getString(R.string.save_pl_dialog_message))
                .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
                }.setPositiveButton(resources.getString(R.string.finish)) { _, _ ->
                    findNavController().navigateUp()
                }

        toolbar.setNavigationOnClickListener {
            if (!nameEditText.text.isNullOrEmpty() || !descriptionEditText.text.isNullOrEmpty() || coverPickedFlag) {
                confirmDialog.show()

            } else {
                findNavController().navigateUp()
            }
        }
    }

    private fun displayView(view: View, displayed: Boolean) {
        if (displayed) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        nameEditText.removeTextChangedListener(plNameTextWatcher)
        descriptionEditText.removeTextChangedListener(plDescriptionTextWatcher)
    }

    private val plNameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(p0: Editable?) {
            createPlTextView.isEnabled = p0?.isNotEmpty() == true
            displayView(nameHint, p0?.isNotEmpty() == true)
        }
    }

    private val plDescriptionTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(p0: Editable?) {
            displayView(descriptionHint, p0?.isNotEmpty() == true)
        }
    }
}