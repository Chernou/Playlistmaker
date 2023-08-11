package com.practicum.playlistmaker.player.ui.add_to_pl_rv

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

class PlaylistsSmallViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val coverImage = itemView.findViewById<ImageView>(R.id.playlist_cover_small)
    private val name = itemView.findViewById<TextView>(R.id.playlist_name_small)
    private val numberOfTracks = itemView.findViewById<TextView>(R.id.number_of_tracks_small)

    fun bind(playlist: Playlist) {
        Glide.with(coverImage)
            .load(playlist.coverUri)
            .fitCenter()
            .apply(
                RequestOptions
                    .bitmapTransform(
                        RoundedCorners(
                            itemView
                                .resources
                                .getDimensionPixelSize(R.dimen.rounded_corners_album_preview)
                        )
                    )
            )
            .placeholder(R.drawable.ic_track_placeholder_small)
            .into(coverImage)
        name.text = playlist.name
        numberOfTracks.text = TextUtils.numberOfTracksString(playlist.numberOfTracks)
    }
}