package com.practicum.playlistmaker.playlist_edit.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.markodevcic.peko.PermissionRequester
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlist_creation.domain.api.db.PlaylistsDbInteractor
import com.practicum.playlistmaker.playlist_creation.domain.api.local_files.PlaylistsFilesInteractor
import com.practicum.playlistmaker.playlist_creation.domain.model.Playlist
import com.practicum.playlistmaker.playlist_creation.view_model.PlaylistCreationViewModel
import com.practicum.playlistmaker.playlist_details.view_model.PlaylistDetails
import com.practicum.playlistmaker.utils.ResourceProvider
import kotlinx.coroutines.launch

class PlaylistEditViewModel(
    private val dbInteractor: PlaylistsDbInteractor,
    filesInteractor: PlaylistsFilesInteractor,
    requester: PermissionRequester,
    private val playlistId: Int,
    private val resourceProvider: ResourceProvider
) : PlaylistCreationViewModel(dbInteractor, filesInteractor, requester) {

    private val playlistLiveData = MutableLiveData<PlaylistDetails>()
    private val playlistEditStringLiveData = MutableLiveData<PlaylistEditStringData>()

    init {
        renderScreen()
    }

    private lateinit var playlist: Playlist

    fun observePlaylistData(): LiveData<PlaylistDetails> = playlistLiveData
    fun observePlaylistEditStringData(): LiveData<PlaylistEditStringData> =
        playlistEditStringLiveData

    private fun renderScreen() {
        viewModelScope.launch {
            playlist = dbInteractor.getPlaylist(playlistId)
            playlistLiveData.value =
                PlaylistDetails(playlist.coverUri, playlist.name, playlist.description)
            playlistEditStringLiveData.value = PlaylistEditStringData(
                resourceProvider.getString(R.string.edit),
                resourceProvider.getString(R.string.save)
            )
        }
    }

    fun onSaveChangesClicked() {
        viewModelScope.launch {
            if (coverUri != playlist.coverUri) dbInteractor
                .editPlaylistUri(playlistId, coverUri)
            if (name != playlist.name) dbInteractor
                .editPlaylistName(playlistId, name)
            if (description != playlist.description) dbInteractor
                .editPlaylistDescription(playlistId, description)
        }
    }

}