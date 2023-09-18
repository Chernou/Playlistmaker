package com.practicum.playlistmaker.utils

import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.utils.DateUtils.formatTime

object TextUtils {

    fun getHighResArtwork(lowResArtworkUri: String) =
        lowResArtworkUri.replaceAfterLast('/', "512x512bb.jpg")

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

    fun getSharedTracksString(tracks: ArrayList<Track>): String {
        var tracksString = getNumberOfTracksString(tracks.size)
        for (i in 0 until tracks.size) {
            tracksString += "\n${i + 1}. ${tracks[i].artistName} - ${tracks[i].trackName} ${
                formatTime(
                    tracks[i].duration
                )
            }"
        }
        return tracksString
    }
}