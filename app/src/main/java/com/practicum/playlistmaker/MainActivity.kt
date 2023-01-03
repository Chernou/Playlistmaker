package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.main_search)
        val mediaButton = findViewById<Button>(R.id.main_media)
        val settingsButton = findViewById<Button>(R.id.main_settings)

        settingsButton.setOnClickListener {
            val searchIntent = Intent(this, SettingsActivity::class.java)
            startActivity(searchIntent)
        }

        mediaButton.setOnClickListener {
            val mediaIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        searchButton.setOnClickListener {
            val settingsIntent = Intent(this, SearchActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}