package com.practicum.playlistmaker.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale

object DateUtils {

    fun formatTime(trackTimeMillis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(trackTimeMillis)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getYear(str: String?): String {
        return LocalDateTime.parse(str).year.toString() //todo seek for solution applicable for API 21
    }
}