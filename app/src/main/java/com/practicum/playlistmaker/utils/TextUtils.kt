package com.practicum.playlistmaker.utils

object TextUtils {

    fun getHighResArtwork(lowResArtworkUri: String) =
        lowResArtworkUri.replaceAfterLast('/', "512x512bb.jpg")
}