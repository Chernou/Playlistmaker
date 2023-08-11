package com.practicum.playlistmaker.media.ui.grid_layout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlists.domain.model.Playlist

class PlaylistsAdapter(private val clickListener: PlaylistClickListener) :
    RecyclerView.Adapter<PlaylistViewHolder>() {

    val playlists = ArrayList<Playlist>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder =
        PlaylistViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.playlist_card_view_big, parent, false)
        )

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener.onPlaylistClickListener(playlists[position]) }
    }

    override fun getItemCount(): Int = playlists.size

    fun interface PlaylistClickListener {
        fun onPlaylistClickListener(playlist: Playlist)
    }
}