package com.vbshkn.ikollect.data.remote.backend.dao

import android.content.Context
import android.net.Uri
import com.vbshkn.ikollect.data.remote.backend.BackendStorage
import com.vbshkn.ikollect.util.now
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.storage.storage
import java.io.File
import javax.inject.Inject

private const val TAG = "BackendStorageDao"
private const val FILE_SCHEMA = "file"

class BackendStorageDao @Inject constructor(
    @ApplicationContext private val context: Context,
    private val supabase: SupabaseClient
) {
    private val storage = supabase.storage

    suspend fun upsertPhotocardImage(uri: Uri, photocardId: Long): String? {
        supabase.auth.awaitInitialization()
        val uid = supabase.auth.currentUserOrNull()?.id

        if (uid == null) {
            android.util.Log.d(TAG, "Failed to upload photocard image: uid is null", )
            return null
        }
        if (uri.scheme != FILE_SCHEMA) {
            android.util.Log.d(TAG, "Failed to upload photocard image: not a valid file path", )
            return null
        }

        val fileBytes = try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            } ?: run {
                android.util.Log.d(TAG, "Failed to upload photocard image: unable to read file bytes", )
                return null
            }
        } catch (e: Exception) {
            android.util.Log.d(TAG, "Failed to upload photocard image: error reading file bytes", e)
            return null
        }
        val pathInStorage = "$uid/pc_$photocardId.jpg"
        try {
            storage.from(BackendStorage.PHOTOCARDS).upload(pathInStorage, fileBytes) { upsert = true }
            val publicUrl = storage.from(BackendStorage.PHOTOCARDS).publicUrl(pathInStorage)
            return "$publicUrl?t=${now()}"
        } catch (e: Exception) {
            android.util.Log.d(TAG, "Failed to upload photocard image: ", e)
            return null
        }
    }

    suspend fun upsertAlbumImage(uri: Uri, albumImageId: Long): String? {
        supabase.auth.awaitInitialization()
        val uid = supabase.auth.currentUserOrNull()?.id

        if (uid == null) {
            android.util.Log.d(TAG, "Failed to upload album image: uid is null")
            return null
        }
        if (uri.scheme != FILE_SCHEMA) {
            android.util.Log.d(TAG, "Failed to upload album image: not a valid file path")
            return null
        }

        val fileBytes = try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            } ?: run {
                android.util.Log.d(TAG, "Failed to upload album image: unable to read file bytes")
                return null
            }
        } catch (e: Exception) {
            android.util.Log.d(TAG, "Failed to upload album image: error reading file bytes", e)
            return null
        }
        val pathInStorage = "$uid/pc_$albumImageId.jpg"
        try {
            storage.from(BackendStorage.ALBUMS).upload(pathInStorage, fileBytes) { upsert = true }
            val publicUrl = storage.from(BackendStorage.ALBUMS).publicUrl(pathInStorage)
            return "$publicUrl?t=${now()}"
        } catch (e: Exception) {
            android.util.Log.d(TAG, "Failed to upload album image: ", e)
            return null
        }
    }
}