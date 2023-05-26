package com.practicum.playlistmaker.settings.view_model

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.settings.domain.ThemeSettings
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.utils.Creator

class SettingsViewModel(
    private val application: App,
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : AndroidViewModel(application) {

    fun onShareAppPressed() {
        sharingInteractor.shareApp()
    }

    fun onSupportPressed() {
        sharingInteractor.openSupport()
    }

    fun onUserAgreementPressed() {
        sharingInteractor.openTerms()
    }

    fun onThemeSwitched(isChecked: Boolean) {
        application.switchTheme(isChecked)
        settingsInteractor.updateThemeSetting(ThemeSettings(isChecked))
    }

    fun darkThemeIsEnabled(): Boolean {
        return settingsInteractor.getThemeSettings().darkThemeEnabled
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as App
                val sharingInteractor =
                    Creator.provideSharingInteractor(application.applicationContext)
                val settingsInteractor =
                    Creator.provideSettingsInteractor(application.applicationContext)
                SettingsViewModel(application, sharingInteractor, settingsInteractor)
            }
        }
    }
}