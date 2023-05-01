package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.practicum.playlistmaker.utils.DateUtils


class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)
    private val artwork: ImageView = itemView.findViewById(R.id.album_artwork)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = DateUtils.formatTime(model.trackTime)
        Glide.with(artwork)
            .load(model.artworkUri)
            .fitCenter()
            .centerCrop()
            .apply(
                RequestOptions
                .bitmapTransform(
                    RoundedCorners(itemView
                    .resources
                    .getDimensionPixelSize(R.dimen.rounded_corners_album_preview))
                ))
            .placeholder(R.drawable.ic_track_placeholder_small)
            .into(artwork)
    }
}