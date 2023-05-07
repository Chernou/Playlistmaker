package com.practicum.playlistmaker.search.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
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

