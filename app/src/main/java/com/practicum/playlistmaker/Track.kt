package com.practicum.playlistmaker

import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.annotations.SerializedName
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.utils.DateUtils.formatTime
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track (
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val country: String,
    val releaseDate: String,
    @SerializedName("trackTimeMillis") val trackTime: Int,
    @SerializedName("artworkUrl100") val artworkUri: String,
    @SerializedName("primaryGenreName") val genre: String,
    @SerializedName("collectionName") val album: String,
    val previewUrl: String?
) : Parcelable

