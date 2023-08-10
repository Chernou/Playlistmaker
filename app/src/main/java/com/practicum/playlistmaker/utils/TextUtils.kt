package com.practicum.playlistmaker.utils

object TextUtils {

    fun getHighResArtwork(lowResArtworkUri: String) =
        lowResArtworkUri.replaceAfterLast('/', "512x512bb.jpg")

    fun getCoverImageName(coverName: String) = "$coverName.jpg"
    fun numberOfTracksString(numberOfTracks: Int): String =
        when (numberOfTracks % 100) {
            1, 21, 31, 41, 51, 61, 71, 81, 91 -> "$numberOfTracks трек"
            2, 22, 32, 42, 52, 62, 72, 82, 92,
            3, 23, 33, 43, 53, 63, 73, 83, 93,
            4, 24, 34, 44, 54, 64, 74, 84, 94 -> "$numberOfTracks трека"

            else -> "$numberOfTracks треков"
        }
}