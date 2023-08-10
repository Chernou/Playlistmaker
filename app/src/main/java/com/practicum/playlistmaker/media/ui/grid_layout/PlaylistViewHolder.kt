package com.practicum.playlistmaker.media.ui.grid_layout

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlists.domain.model.Playlist
import com.practicum.playlistmaker.utils.TextUtils

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val cover: ImageView = itemView.findViewById(R.id.playlist_cover)
    private val name: TextView = itemView.findViewById(R.id.playlist_name)
    private val numberOfTracks: TextView = itemView.findViewById(R.id.number_of_tracks)

    fun bind(playlist: Playlist) {
        Glide.with(cover)
            .load(playlist.coverUri)
            .fitCenter()
            .apply(
                RequestOptions
                    .bitmapTransform(
                        RoundedCorners(
                            itemView
                                .resources
                                .getDimensionPixelSize(R.dimen.rounded_corners_album)
                        )
                    )
            )
            .placeholder(R.drawable.ic_track_placeholder_large)
            .into(cover)
        name.text = playlist.name
        numberOfTracks.text = TextUtils.numberOfTracksString(playlist.numberOfTracks)
    }

}