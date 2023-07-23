package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.navigatorModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.resourceProviderModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.settings.data.api.SettingsRepository
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    private val repository: SettingsRepository by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                repositoryModule,
                dataModule,
                interactorModule,
                resourceProviderModule,
                navigatorModule,
                viewModelModule,
            )
        }
        repository.applyAppTheme()
    }
}