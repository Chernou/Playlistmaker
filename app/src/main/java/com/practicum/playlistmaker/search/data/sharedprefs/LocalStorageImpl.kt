package com.practicum.playlistmaker.search.data.sharedprefs

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.data.LocalStorage
import com.practicum.playlistmaker.search.domain.Track

class LocalStorageImpl(private val sharedPreferences: SharedPreferences, private val gson: Gson) :
    LocalStorage {

    override fun getSearchHistory(): ArrayList<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY, null)
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, type) ?: ArrayList()
    }

    override fun addTrackToSearchHistory(track: Track) {
        val searchHistory = getSearchHistory()
        if (searchHistory.contains(track)) {
            searchHistory.remove(track)
            searchHistory.add(0, track)
        } else {
            if (searchHistory.size == 10) {
                searchHistory.removeAt(9)
                searchHistory.add(0, track)
            } else {
                searchHistory.add(0, track)
            }
        }
        saveSearchHistory(searchHistory)
    }

    override fun clearSearchHistory() {
        saveSearchHistory(ArrayList())
    }

    private fun saveSearchHistory(searchHistory: List<Track>) {
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY, Gson().toJson(searchHistory))
            .apply()
    }

    companion object {
        private const val SEARCH_HISTORY = "SEARCH_HISTORY"
    }
}