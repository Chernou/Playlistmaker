package com.practicum.playlistmaker.search.data.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.SearchRequest
import org.koin.core.component.KoinComponent

class RetrofitNetworkClient : NetworkClient, KoinComponent {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun doRequest(dto: Any): Response {
        val itunesService: ItunesService = getKoin().get()
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTIVITY_ERROR }
        }
        if (dto !is SearchRequest) {
            return Response().apply { resultCode = BAD_REQUEST_ERROR }
        }
        val response = itunesService.search(dto.query).execute()
        val body = response.body()
        return body?.apply { resultCode = response.code() } ?: Response().apply {
            resultCode = response.code()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isConnected(): Boolean {
        val connectivityManager: ConnectivityManager = getKoin().get()
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }

    companion object {
        const val BASE_URL = "https://itunes.apple.com"
        const val BAD_REQUEST_ERROR = 400
        const val NO_CONNECTIVITY_ERROR = -1
    }
}