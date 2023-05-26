package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.settings.data.api.SettingsRepository
import com.practicum.playlistmaker.utils.Creator

class App : Application() {

    private lateinit var repository: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        repository = Creator.provideSettingsRepository(this.applicationContext)
        repository.applyAppTheme()
    }
}