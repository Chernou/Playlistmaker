package com.practicum.playlistmaker.media.view_model

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.utils.ResourceProvider

class PlaylistsViewModel(resourceProvider: ResourceProvider) : ViewModel() {

    private val textLiveData = MutableLiveData<String>()

    init {
        textLiveData.postValue(resourceProvider.getString(R.string.no_playlists))
    }

    fun observeText(): LiveData<String> = textLiveData

}
