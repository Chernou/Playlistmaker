package com.practicum.playlistmaker.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    fun formatTime(trackTimeMillis: Int?): String = SimpleDateFormat("mm:ss", Locale.getDefault())
        .format(trackTimeMillis) ?: ""

    fun getYear(str: String?): String? {
        return if (str == null) {
            null
        } else {
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(str)
            val calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
                time = date
            }
            calendar.get(Calendar.YEAR).toString()
        }
    }
}