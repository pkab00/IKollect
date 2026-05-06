package com.vbshkn.ikollect.data.local.datastore


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val PREFS_NAME = preferencesDataStore("service_log")

class ServiceLogStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.localDataStore by PREFS_NAME

    private object Keys {
        val LAST_SYNC_TIMESTAMP = stringPreferencesKey("last_sync_timestamp")
    }

    fun getLastSyncTimestamp(): Flow<String?> {
        return context.localDataStore.data.map { preferences ->
            preferences[Keys.LAST_SYNC_TIMESTAMP]
        }
    }

    suspend fun updateLastSyncTimestamp(new: String) {
        context.localDataStore.edit { preferences ->
            preferences[Keys.LAST_SYNC_TIMESTAMP] = new
        }
    }
}