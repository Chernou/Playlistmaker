package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName
import com.practicum.playlistmaker.Track
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesService {

    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<SearchResponse>

}

class SearchResponse(@SerializedName("results") var trackList: List<Track>)