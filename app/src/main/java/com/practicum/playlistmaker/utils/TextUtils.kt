package com.practicum.playlistmaker.utils

object TextUtils {

    fun removeLastChar(str: String?): String? {
        return str?.replaceFirst(".$".toRegex(), "")
    }

    fun getHighResArtwork(lowResArtworkUri: String): String {
        return lowResArtworkUri.replaceAfterLast('/', "512x512bb.jpg")
    }
}