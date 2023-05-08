package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.SearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient() : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesService::class.java)

    override fun doRequest(dto: Any): Response {
        return if (dto is SearchRequest) {
            val response = itunesService.search(dto.query).execute()
            val body = response.body() ?: Response()
            body.apply { resultCode = response.code() }
        } else {
            Response().apply { resultCode = BAD_REQUEST_ERROR }
        }
    }

    companion object {
        const val BASE_URL = "https://itunes.apple.com"
        const val BAD_REQUEST_ERROR = 400
    }


}