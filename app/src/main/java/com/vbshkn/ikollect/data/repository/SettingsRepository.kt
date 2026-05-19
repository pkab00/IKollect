package com.vbshkn.ikollect.data.repository

import com.vbshkn.ikollect.data.local.datastore.LocalLanguage
import com.vbshkn.ikollect.data.local.datastore.LocalSettingsStorage
import com.vbshkn.ikollect.data.local.datastore.LocalTabs
import com.vbshkn.ikollect.data.local.datastore.LocalTheme
import com.vbshkn.ikollect.data.mapper.toDomain
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val localSettingsStorage: LocalSettingsStorage
) {
    fun getSettings() = localSettingsStorage.getSettingsFlow().map { it.toDomain() }

    suspend fun getCurrentSettings() = localSettingsStorage.getCurrentSettings().toDomain()

    suspend fun updateLanguage(language: LocalLanguage) {
        localSettingsStorage.updateLanguage(language)
    }

    suspend fun updateTheme(theme: LocalTheme) {
        localSettingsStorage.updateTheme(theme)
    }

    suspend fun updateTabsOrder(order: List<LocalTabs>) {
        localSettingsStorage.updateTabsOrder(order)
    }
}