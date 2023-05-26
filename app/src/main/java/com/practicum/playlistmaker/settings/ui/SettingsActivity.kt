package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.view_model.SettingsViewModel
import com.practicum.playlistmaker.utils.NavigationRouter
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        val router: NavigationRouter = getKoin().get {
            parametersOf(this)
        }
        toolbar.setNavigationOnClickListener { router.goBack() }

        val shareAppTextView = findViewById<TextView>(R.id.share_app)
        val supportTextView = findViewById<TextView>(R.id.support)
        val userAgreementTextView = findViewById<TextView>(R.id.user_agreement)
        val themeSwitch = findViewById<SwitchMaterial>(R.id.switch_dark_theme)

        themeSwitch.isChecked = viewModel.darkThemeIsEnabled()

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