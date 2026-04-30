package com.vbshkn.ikollect.data.remote.backend.dao

import com.vbshkn.ikollect.data.remote.backend.BackendStorage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.storage.storage
import java.io.File
import javax.inject.Inject

private const val TAG = "BackendStorageDao"

class BackendStorageDao @Inject constructor(
    private val supabase: SupabaseClient
) {
    private val storage = supabase.storage

    suspend fun upsertPhotocardImage(uri: String, photocardId: Long): String? {
        supabase.auth.awaitInitialization()
        val uid = supabase.auth.currentUserOrNull()?.id

        if (uid == null) {
            android.util.Log.d(TAG, "Failed to upload photocard image: uid is null", )
            return null
        }
        if (!uri.startsWith("file:///")) {
            android.util.Log.d(TAG, "Failed to upload photocard image: not a valid file path", )
            return null
        }

        val purePath = uri.removePrefix("file://")
        val fileBytes = File(purePath).readBytes()
        val pathInStorage = "$uid/pc_$photocardId.jpg"
        try {
            storage.from(BackendStorage.PHOTOCARDS).upload(pathInStorage, fileBytes)
            return storage.from(BackendStorage.PHOTOCARDS).publicUrl(pathInStorage)
        } catch (e: Exception) {
            android.util.Log.d(TAG, "Failed to upload photocard image: ", e)
            return null
        }
    }

    suspend fun upsertAlbumImage(uri: String, albumImageId: Long): String? {
        supabase.auth.awaitInitialization()
        val uid = supabase.auth.currentUserOrNull()?.id

        if (uid == null) {
            android.util.Log.d(TAG, "Failed to upload album image: uid is null", )
            return null
        }
        if (!uri.startsWith("file:///")) {
            android.util.Log.d(TAG, "Failed to upload album image: not a valid file path", )
            return null
        }

        val fileBytes = File(uri).readBytes()
        val pathInStorage = "$uid/pc_$albumImageId.jpg"
        try {
            storage.from(BackendStorage.ALBUMS).upload(pathInStorage, fileBytes)
            return storage.from(BackendStorage.ALBUMS).publicUrl(pathInStorage)
        } catch (e: Exception) {
            android.util.Log.d(TAG, "Failed to upload album image: ", e)
            return null
        }
    }
}