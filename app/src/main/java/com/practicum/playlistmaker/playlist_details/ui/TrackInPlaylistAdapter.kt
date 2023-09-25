package com.practicum.playlistmaker.playlist_details.ui

import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.search.ui.TrackViewHolder

class TrackInPlaylistAdapter<TrackInPlaylistViewHolder : TrackViewHolder>(private val clickListener: TrackClickListener) :
    TrackAdapter<TrackInPlaylistViewHolder>(clickListener) {

    override fun onBindViewHolder(holder: TrackInPlaylistViewHolder, position: Int) {
        holder.itemView.setOnLongClickListener { clickListener.onTrackLongClickListener(trackList[position]) }
        super.onBindViewHolder(holder, position)
    }

    interface TrackClickListener : TrackAdapter.TrackClickListener {
        fun onTrackLongClickListener(track: Track): Boolean
    }
}