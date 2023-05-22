package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.data.api.SettingsRepository
import com.practicum.playlistmaker.utils.Creator

class App : Application() {

    private lateinit var repository: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        repository = Creator.provideSettingsRepository(this.applicationContext)
        switchTheme(repository.getThemeSettings().darkThemeEnabled)
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
}