package com.practicum.playlistmaker.utils

import android.app.Activity

class NavigationRouter(
    private val activity: Activity
) {

    fun goBack() {
        activity.onBackPressed()
    }
}