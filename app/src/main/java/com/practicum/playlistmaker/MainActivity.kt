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
            onButtonClick(SettingsActivity::class.java)
        }

        mediaButton.setOnClickListener {
            onButtonClick(MediaActivity::class.java)
        }

        searchButton.setOnClickListener {
            onButtonClick(SearchActivity::class.java)
        }
    }

    private fun onButtonClick(targetClass: Class<out AppCompatActivity>) {
        val intent = Intent(this, targetClass)
        startActivity(intent)
    }

}