package com.practicum.playlistmaker.utils

import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {

    fun formatTime(trackTimeMillis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(trackTimeMillis)
    }
}