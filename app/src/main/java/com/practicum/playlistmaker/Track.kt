package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class Track(
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: Int,
    @SerializedName("artworkUrl100") val artworkUri: String
)

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)
    private val artwork: ImageView = itemView.findViewById(R.id.album_artwork)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTime)
        Glide.with(artwork)
            .load(model.artworkUri)
            .fitCenter()
            .centerCrop()
            .apply(RequestOptions.bitmapTransform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.rounded_corners_album))))
            .placeholder(R.drawable.ic_track_placeholder)
            .into(artwork)
    }
}
