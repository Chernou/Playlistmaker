package com.practicum.playlistmaker.utils

import androidx.activity.ComponentActivity

class NavigationRouter(
    private val activity: ComponentActivity
) {

    fun goBack() {
        activity.onBackPressedDispatcher.onBackPressed()
    }
}