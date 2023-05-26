package com.practicum.playlistmaker.search.data.api

interface ResourceProvider {
    fun getString(resId: Int): String
}