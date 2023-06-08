package com.practicum.playlistmaker.utils

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources

class ResourceProviderImpl(private val context: Context) : ResourceProvider {
    override fun getString(resId: Int) = context.getString(resId)
}