package com.practicum.playlistmaker.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.MediaActivity
import com.practicum.playlistmaker.search.ui.SearchActivity
import com.practicum.playlistmaker.settings.ui.SettingsActivity
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.main_search)
        val mediaButton = findViewById<Button>(R.id.main_media)
        val settingsButton = findViewById<Button>(R.id.main_settings)

        settingsButton.setOnClickListener {
            openScreen(SettingsActivity::class.java)
        }

        mediaButton.setOnClickListener {
            openScreen(MediaActivity::class.java)
        }

        searchButton.setOnClickListener {
            openScreen(SearchActivity::class.java)
        }
    }

    private fun openScreen(targetClass: Class<out Activity>) {
        val intent: Intent = getKoin().get{
            parametersOf(this, targetClass)
        }
        startActivity(intent)
    }
}