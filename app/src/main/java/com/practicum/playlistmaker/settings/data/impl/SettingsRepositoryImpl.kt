package com.practicum.playlistmaker.settings.data.impl

import android.content.SharedPreferences
import com.practicum.playlistmaker.settings.data.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.ThemeSettings

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        return (ThemeSettings(sharedPreferences.getBoolean(DARK_THEME_ENABLED, false)))
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_ENABLED, settings.darkThemeEnabled)
            .apply()
    }

    companion object {
        const val DARK_THEME_ENABLED = "DARK_THEME_ENABLED"
    }
}