package com.practicum.playlistmaker.utils

import com.practicum.playlistmaker.playlist_creation.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.utils.DateUtils.formatTime

object TextUtils {

    fun getHighResArtwork(midResArtworkUri: String) =
        midResArtworkUri.replaceAfterLast('/', "512x512bb.jpg")

    fun getLowResArtwork(midResArtworkUri: String) =
        midResArtworkUri.replaceAfterLast('/', "512x512bb.jpg")

    fun getNumberOfTracksString(numberOfTracks: Int): String =
        when (numberOfTracks % 100) {
            in (5..20) -> "$numberOfTracks треков"
            else -> when (numberOfTracks % 10) {
                1 -> "$numberOfTracks трек"
                in (2..4) -> "$numberOfTracks трека"
                else -> "$numberOfTracks треков"
            }
        }

    fun getTotalMinutesString(minutes: Int): String =
        when (minutes % 100) {
            in (5..20) -> "$minutes минут"
            else -> when (minutes % 10) {
                1 -> "$minutes минута"
                in (2..4) -> "$minutes минуты"
                else -> "$minutes минут"
            }
        }

    fun getSharedTracksString(playlist: Playlist, tracks: ArrayList<Track>): String {
        val tracksString = buildString {
            append(playlist.name)
            appendLine()
            append(playlist.description)
            appendLine()
            append(getNumberOfTracksString(tracks.size))
            for (i in 0 until tracks.size) {
                appendLine()
                append("${i + 1}. ${tracks[i].artistName} - ${tracks[i].trackName} ${
                    formatTime(
                        tracks[i].duration
                    )
                }")
            }
        }
        return tracksString
    }
}