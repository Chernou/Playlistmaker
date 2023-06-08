package com.practicum.playlistmaker.utils

import android.graphics.drawable.Drawable

interface ResourceProvider {
    fun getString(resId: Int): String
}