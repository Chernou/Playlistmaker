package com.practicum.playlistmaker.search.data.impl

import android.content.Context
import com.practicum.playlistmaker.search.data.api.ResourceProvider

class ResourceProviderImpl(private val context: Context) : ResourceProvider {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }
}