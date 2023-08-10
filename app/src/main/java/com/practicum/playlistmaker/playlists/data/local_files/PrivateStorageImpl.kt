package com.practicum.playlistmaker.playlists.data.local_files

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream

class PrivateStorageImpl(private val context: Context) : PrivateStorage {
    override suspend fun saveImage(uri: Uri, name: String) {
        val filePath = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            PLAYLIST_COVERS_FOLDER
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, uri.getOriginalFileName(context)!!)
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = context.contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    override suspend fun getImage() {
        TODO("Not yet implemented")
    }

    private fun Uri.getOriginalFileName(context: Context): String? {
        return context.contentResolver.query(this, null, null, null, null)?.use {
            val nameColumnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameColumnIndex)
        }
    }

    companion object {
        const val PLAYLIST_COVERS_FOLDER = "playlists_covers"
    }
}