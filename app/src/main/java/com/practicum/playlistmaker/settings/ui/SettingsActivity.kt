package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.view_model.SettingsViewModel
import com.practicum.playlistmaker.utils.Creator

class SettingsActivity : AppCompatActivity() {

    private val router = Creator.provideNavigationRouter(this)
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        toolbar.setNavigationOnClickListener { router.goBack() }

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]

        val shareAppTextView = findViewById<TextView>(R.id.share_app)
        val supportTextView = findViewById<TextView>(R.id.support)
        val userAgreementTextView = findViewById<TextView>(R.id.user_agreement)
        val themeSwitch = findViewById<SwitchMaterial>(R.id.switch_dark_theme)

        themeSwitch.isChecked = viewModel.getThemeSettings()

        shareAppTextView.setOnClickListener {
            viewModel.onShareAppPressed()
        }

        supportTextView.setOnClickListener {
            viewModel.onSupportPressed()
        }

        userAgreementTextView.setOnClickListener {
            viewModel.onUserAgreementPressed()
        }

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitched(isChecked)
        }
    }
}