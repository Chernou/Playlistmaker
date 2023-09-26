package com.practicum.playlistmaker.playlist_creation.data.local_files

import android.net.Uri
import java.net.URI
import kotlin.coroutines.CoroutineContext

interface PrivateStorage {
    suspend fun saveImage(coroutineContext: CoroutineContext, uri: Uri): URI
    suspend fun deleteFromPrivateStorage(coroutineContext: CoroutineContext, coverUri: String)
}