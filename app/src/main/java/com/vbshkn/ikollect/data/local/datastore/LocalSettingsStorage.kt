package com.vbshkn.ikollect.data.local.datastore

import android.content.Context
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.vbshkn.ikollect.util.now
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject


class LocalSettingsStorage (
    private val context: Context
) {
    private val Context.settingsDataStore by dataStore(
        fileName = "local_settings.json",
        serializer = LocalSettingsSerializer
    )

    fun getSettingsFlow() = context.settingsDataStore.data

    suspend fun getCurrentSettings() = context.settingsDataStore.data.first()

    suspend fun updateTheme(theme: LocalTheme) {
        context.settingsDataStore.updateData { current ->
            current.copy(theme = theme)
        }
    }

    suspend fun updateLanguage(language: LocalLanguage) {
        context.settingsDataStore.updateData { current ->
            current.copy(language = language)
        }
    }

    suspend fun updateTabsOrder(tabsOrder: List<LocalTabs>) {
        context.settingsDataStore.updateData { current ->
            current.copy(tabsOrder = tabsOrder)
        }
    }

    suspend fun update(settings: LocalSettings) {
        updateTheme(settings.theme)
        updateLanguage(settings.language)
        updateTabsOrder(settings.tabsOrder)
    }

    object LocalSettingsSerializer : Serializer<LocalSettings> {
        override val defaultValue: LocalSettings = LocalSettings()

        override suspend fun readFrom(input: InputStream): LocalSettings {
            return try {
                Json.decodeFromString(
                    deserializer = LocalSettings.serializer(),
                    string = input.readBytes().decodeToString()
                )
            } catch (e: Exception) {
                defaultValue
            }
        }

        override suspend fun writeTo(
            t: LocalSettings,
            output: OutputStream
        ) {
            withContext(Dispatchers.IO) {
                output.write(
                    Json.encodeToString(
                        serializer = LocalSettings.serializer(),
                        value = t
                    ).encodeToByteArray()
                )
            }
        }
    }
}