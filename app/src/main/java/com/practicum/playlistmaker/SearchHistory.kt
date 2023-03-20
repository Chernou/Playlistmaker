package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPref: SharedPreferences) {

    var searchHistoryTrackList: ArrayList<Track> = initializedSearchedHistory()

    init {
        searchHistoryTrackList = initializedSearchedHistory()
    }

    private fun initializedSearchedHistory(): ArrayList<Track> {
        val json = sharedPref.getString(SEARCH_HISTORY, null)
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return Gson().fromJson(json, type) ?: ArrayList()
    }


    fun addTrack(track: Track) : Boolean {
        if (searchHistoryTrackList.contains(track)) {
            searchHistoryTrackList.remove(track)
            searchHistoryTrackList.add(0, track)
            return true
        } else {
            if (searchHistoryTrackList.size == 10) {
                searchHistoryTrackList.removeAt(9)
                searchHistoryTrackList.add(0, track)
                return true
            } else {
                searchHistoryTrackList.add(0, track)
                return false
            }
        }
    }

    fun clearSearchHistory() {
        searchHistoryTrackList.clear()
    }

    fun updateSharedPref() {
        sharedPref.edit()
            .putString(SEARCH_HISTORY, Gson().toJson(searchHistoryTrackList))
            .apply()
    }

    companion object {
        const val SEARCH_HISTORY = "SEARCH_HISTORY"
    }
}