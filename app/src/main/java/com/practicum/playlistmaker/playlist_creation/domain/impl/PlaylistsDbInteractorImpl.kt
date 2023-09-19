package com.practicum.playlistmaker.playlist_creation.domain.impl

import com.practicum.playlistmaker.playlist_creation.domain.api.db.PlaylistsDbInteractor
import com.practicum.playlistmaker.playlist_creation.domain.api.db.PlaylistsRepository
import com.practicum.playlistmaker.playlist_creation.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsDbInteractorImpl(private val repository: PlaylistsRepository) :
    PlaylistsDbInteractor {

    override suspend fun addPlaylist(playlist: Playlist) {
        repository.addPlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> = repository.getPlaylists()

    override suspend fun addTrackToPl(track: Track, playlist: Playlist) {
        repository.addTrackToPl(track, playlist)
    }

    override suspend fun getPlaylist(playlistId: Int): Playlist = repository.getPlaylist(playlistId)

    override suspend fun editPlaylistUri(playlistId: Int, coverUri: String) {
        repository.editPlaylistUri(playlistId, coverUri)
    }

    override suspend fun editPlaylistName(playlistId: Int, name: String) {
        repository.editPlaylistName(playlistId, name)
    }

    override suspend fun editPlaylistDescription(playlistId: Int, description: String) {
        repository.editPlaylistDescription(playlistId, description)
    }
}