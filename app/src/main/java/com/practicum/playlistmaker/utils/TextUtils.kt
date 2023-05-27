package com.practicum.playlistmaker.utils

object TextUtils {

    fun removeLastChar(str: String?): String? = str?.replaceFirst(".$".toRegex(), "")

    fun getHighResArtwork(lowResArtworkUri: String) =
        lowResArtworkUri.replaceAfterLast('/', "512x512bb.jpg")
}