package com.vbshkn.ikollect.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class ImageRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun saveCacheToInternalStorage(cacheUriString: String): String {
        val cacheUri = cacheUriString.toUri()
        val cacheFile = File(cacheUri.path!!)

        val permanentName = "cover_${System.currentTimeMillis()}.jpg"
        val permanentFile = File(context.filesDir, permanentName)

        cacheFile.inputStream().use { input ->
            permanentFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        cacheFile.delete()
        return Uri.fromFile(permanentFile).toString()
    }
}