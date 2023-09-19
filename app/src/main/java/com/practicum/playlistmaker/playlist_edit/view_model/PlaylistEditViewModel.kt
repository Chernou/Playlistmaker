package com.practicum.playlistmaker.playlist_edit.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.markodevcic.peko.PermissionRequester
import com.practicum.playlistmaker.playlist_details.view_model.PlaylistDetails
import com.practicum.playlistmaker.playlist_creation.domain.api.db.PlaylistsDbInteractor
import com.practicum.playlistmaker.playlist_creation.domain.api.local_files.PlaylistsFilesInteractor
import com.practicum.playlistmaker.playlist_creation.domain.model.Playlist
import com.practicum.playlistmaker.playlist_creation.view_model.PlaylistCreationViewModel
import kotlinx.coroutines.launch

class PlaylistEditViewModel(
    private val dbInteractor: PlaylistsDbInteractor,
    filesInteractor: PlaylistsFilesInteractor,
    requester: PermissionRequester,
    private val playlistId: Int
) : PlaylistCreationViewModel(dbInteractor, filesInteractor, requester) {

    init {
        renderScreen()
    }

    private lateinit var playlist: Playlist

    private val playlistLiveData = MutableLiveData<PlaylistDetails>()
    fun observePlaylistData(): LiveData<PlaylistDetails> = playlistLiveData

    private fun renderScreen() {
        viewModelScope.launch {
            playlist = dbInteractor.getPlaylist(playlistId)
            playlistLiveData.value =
                PlaylistDetails(playlist.coverUri, playlist.name, playlist.description)
        }
    }

}