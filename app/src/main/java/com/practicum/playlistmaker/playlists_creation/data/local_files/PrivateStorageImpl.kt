package com.practicum.playlistmaker.playlists_creation.data.local_files

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.net.URI

class PrivateStorageImpl(private val context: Context) : PrivateStorage {
    override suspend fun saveImage(uri: Uri): URI {
        val filePath = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            PLAYLISTS_COVERS_FOLDER
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, uri.lastPathSegment ?: "image")
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream)

        return file.toURI()
    }

    companion object {
        const val PLAYLISTS_COVERS_FOLDER = "playlists_covers"
        const val IMAGE_QUALITY = 30
    }
}