package com.practicum.playlistmaker.utils

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
}