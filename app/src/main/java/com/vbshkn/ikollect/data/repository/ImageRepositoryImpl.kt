package com.vbshkn.ikollect.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.vbshkn.ikollect.domain.repository.ImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageRepository {
    override fun saveToInternalStorage(uriString: String): String {
        val uri = uriString.toUri()
        val permanentName = "cover_${System.currentTimeMillis()}.jpg"
        val permanentFile = File(context.filesDir, permanentName)

        if (uri.scheme == "content") {
            context.contentResolver.openInputStream(uri)?.use { input ->
                permanentFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: throw IllegalArgumentException("Unable to open content URI: $uri")
        } else {
            val filePath = uri.path ?: uriString
            File(filePath).inputStream().use { input ->
                permanentFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }

        android.util.Log.d("ImageRepository", "Saved file $permanentName")
        return Uri.fromFile(permanentFile).toString()
    }

    override fun deleteFromInternalStorage(uriString: String) {
        try {
            val fileUri = uriString.toUri()
            val file = File(fileUri.path ?: throw IllegalArgumentException("Invalid URI: $fileUri"))
            if (file.exists()) {
                file.delete()
                android.util.Log.d("ImageRepository", "Deleted file ${file.name}")
            }
        } catch (e: Exception) {
            android.util.Log.d("ImageRepository", "Failed to delete: ${e.message}")
        }
    }

    override fun clearLocalStorage() {
        val localDir = context.filesDir
        localDir.listFiles()?.forEach { file ->
            file.delete()
        }
    }
}