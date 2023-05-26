package com.practicum.playlistmaker.settings.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.settings.domain.ThemeSettings
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.utils.Creator

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

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
        settingsInteractor.updateThemeSetting(ThemeSettings(isChecked))
    }

    fun darkThemeIsEnabled(): Boolean {
        return settingsInteractor.getThemeSettings().darkThemeEnabled
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val sharingInteractor =
                    Creator.provideSharingInteractor(context)
                val settingsInteractor =
                    Creator.provideSettingsInteractor(context)
                SettingsViewModel(sharingInteractor, settingsInteractor)
            }
        }
    }
}