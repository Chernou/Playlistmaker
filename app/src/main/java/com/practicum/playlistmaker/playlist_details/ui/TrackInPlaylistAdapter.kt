package com.practicum.playlistmaker.playlist_details.ui

import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.search.ui.TrackViewHolder

class TrackInPlaylistAdapter(private val clickListener: TrackClickListener) : TrackAdapter(clickListener) {

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.itemView.setOnLongClickListener { clickListener.onTrackLongClickListener(trackList[position]) }
        super.onBindViewHolder(holder, position)
    }

    interface TrackClickListener : TrackAdapter.TrackClickListener {
        fun onTrackLongClickListener(track: Track): Boolean
    }
}