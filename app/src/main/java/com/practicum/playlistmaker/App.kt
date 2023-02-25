package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    lateinit var sharedPref: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPref = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE)
        switchTheme(sharedPref.getBoolean(DARK_THEME_ENABLED, false))
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        const val SHARED_PREFERENCE = "SHARED_PREFERENCE"
        const val DARK_THEME_ENABLED = "DARK_THEME_ENABLED"
    }
}