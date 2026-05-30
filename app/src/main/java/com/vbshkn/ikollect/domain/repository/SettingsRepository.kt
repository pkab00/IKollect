package com.vbshkn.ikollect.domain.repository

import com.vbshkn.ikollect.data.local.datastore.LocalLanguage
import com.vbshkn.ikollect.data.local.datastore.LocalTabs
import com.vbshkn.ikollect.data.local.datastore.LocalTheme
import com.vbshkn.ikollect.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>

    suspend fun getCurrentSettings(): AppSettings

    suspend fun updateLanguage(language: LocalLanguage)

    suspend fun updateTheme(theme: LocalTheme)

    suspend fun updateTabsOrder(order: List<LocalTabs>)
}