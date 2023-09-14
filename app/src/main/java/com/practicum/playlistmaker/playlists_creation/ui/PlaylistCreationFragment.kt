package com.practicum.playlistmaker.playlists_creation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlists_creation.view_model.PermissionState
import com.practicum.playlistmaker.playlists_creation.view_model.PlaylistsCreationViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistCreationFragment : Fragment() {

    private val viewModel: PlaylistsCreationViewModel by viewModel()
    private lateinit var nameEditText: TextInputEditText
    private lateinit var descriptionEditText: TextInputEditText
    private lateinit var nameContainer: TextInputLayout
    private lateinit var descriptionContainer: TextInputLayout
    private lateinit var coverImageView: ImageView
    private lateinit var createPlTextView: TextView

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
        nameContainer = view.findViewById(R.id.playlist_name_layout)
        descriptionContainer = view.findViewById(R.id.playlist_description_layout)

        var coverPickedFlag = false
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Glide.with(coverImageView)
                        .load(uri.toString())
                        .centerCrop()
                        .into(coverImageView)
                    coverPickedFlag = true
                }
                viewModel.saveCoverUri(uri)
            }

        viewModel.observePermissionState().observe(viewLifecycleOwner) { state ->
            when (state) {
                PermissionState.GRANTED -> pickMedia.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )

                PermissionState.DENIED_PERMANENTLY -> displaySettings()
                else -> permissionRationaleToast()
            }
        }

        coverImageView.setOnClickListener {
            viewModel.onCoverClicked()
        }

        nameEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onNameChanged(text)
            createPlTextView.isEnabled = text?.isNotEmpty() == true
            setHintAndBoxColor(text, nameContainer)
        }

        descriptionEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onDescriptionChanged(text)
            setHintAndBoxColor(text, descriptionContainer)
        }

        createPlTextView.setOnClickListener {
            viewModel.onCreatePlClicked()
            findNavController().navigateUp()
            playlistCreatedSnackbar(view.findViewById(R.id.parent_layout))
        }

        val confirmDialog =
            MaterialAlertDialogBuilder(requireContext(), R.style.AppTheme_MyMaterialAlertDialog)
                .setTitle(resources.getString(R.string.save_pl_dialog_title))
                .setMessage(resources.getString(R.string.save_pl_dialog_message))
                .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
                }.setPositiveButton(resources.getString(R.string.finish)) { _, _ ->
                    findNavController().navigateUp()
                }

        toolbar.setNavigationOnClickListener {
            viewModel
            if (!nameEditText.text.isNullOrEmpty() || !descriptionEditText.text.isNullOrEmpty() || coverPickedFlag) {
                confirmDialog.show()
            } else {
                findNavController().navigateUp()
            }
        }
    }

    private fun playlistCreatedSnackbar(view: View) {
        Snackbar.make(
            requireContext(),
            view,
            "${getString(R.string.playlist)} ${nameEditText.text} ${getString(R.string.created)}",
            Snackbar.LENGTH_LONG
        ).setBackgroundTint(
            ContextCompat.getColor(
                requireContext(),
                R.color.black_light_white_night
            )
        ).show()
    }

    private fun displaySettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.fromParts("package", requireContext().packageName, null)
        requireContext().startActivity(intent)
    }

    private fun permissionRationaleToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.read_image_rationale),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun setHintAndBoxColor(text: CharSequence?, view: TextInputLayout) {
        if (text.isNullOrEmpty()) {
            view.defaultHintTextColor =
                ContextCompat.getColorStateList(requireContext(), R.color.pl_creation_edit_text)
            ContextCompat.getColorStateList(requireContext(), R.color.pl_creation_edit_text)
                ?.let { view.setBoxStrokeColorStateList(it) }
        } else {
            view.defaultHintTextColor =
                ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.pl_creation_edit_text_blue
                )
            ContextCompat.getColorStateList(requireContext(), R.color.pl_creation_edit_text_blue)
                ?.let { view.setBoxStrokeColorStateList(it) }
        }
    }

}