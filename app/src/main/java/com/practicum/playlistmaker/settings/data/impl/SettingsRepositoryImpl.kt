package com.practicum.playlistmaker.settings.data.impl

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.data.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.ThemeSettings

class SettingsRepositoryImpl(context: Context) :
    SettingsRepository {

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE)

    override fun getThemeSettings(): ThemeSettings {

        return (ThemeSettings(sharedPreferences.getBoolean(DARK_THEME_ENABLED, false)))
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_ENABLED, settings.darkThemeEnabled)
            .apply()
        applyAppTheme()
    }

    override fun applyAppTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (getThemeSettings().darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        private const val DARK_THEME_ENABLED = "DARK_THEME_ENABLED"
        private const val SHARED_PREFERENCE = "SHARED_PREFERENCE"
    }
}