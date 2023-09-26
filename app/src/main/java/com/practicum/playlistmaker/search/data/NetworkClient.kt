package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.data.dto.Response
import kotlin.coroutines.CoroutineContext

interface NetworkClient {
    suspend fun doRequest(coroutineContext: CoroutineContext, dto: Any): Response
}