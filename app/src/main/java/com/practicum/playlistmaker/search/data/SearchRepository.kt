package com.practicum.playlistmaker.search.data

import android.annotation.SuppressLint
import android.util.Log
import com.practicum.playlistmaker.ItunesService
import com.practicum.playlistmaker.SearchResponse
import com.practicum.playlistmaker.search.domain.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchRepository(
    private val itunesService: ItunesService
) {
    fun searchTracks(searchRequest: String, onSuccess: (List<Track>) -> Unit, onFailure: () -> Unit) {
        itunesService.search(searchRequest).enqueue(object : Callback<SearchResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                if (response.isSuccessful) {
                    onSuccess.invoke(response.body()?.trackList!!)
                    Log.d("!@#", response.code().toString())
                } else {
                    onFailure.invoke()
                    Log.d("!@#", response.code().toString())
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                onFailure.invoke()
                Log.d("!@#", "Критическая ошибка")
                Log.d("!@#", t.message.toString())
            }
        })
    }
}