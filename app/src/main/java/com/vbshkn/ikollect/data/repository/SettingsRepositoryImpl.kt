package com.vbshkn.ikollect.data.repository

import com.vbshkn.ikollect.data.local.datastore.LocalLanguage
import com.vbshkn.ikollect.data.local.datastore.LocalSettingsStorage
import com.vbshkn.ikollect.data.local.datastore.LocalTabs
import com.vbshkn.ikollect.data.local.datastore.LocalTheme
import com.vbshkn.ikollect.data.mapper.toDomain
import com.vbshkn.ikollect.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val localSettingsStorage: LocalSettingsStorage
) : SettingsRepository {
    override fun getSettings() = localSettingsStorage.getSettingsFlow().map { it.toDomain() }

    override suspend fun getCurrentSettings() = localSettingsStorage.getCurrentSettings().toDomain()

    override suspend fun updateLanguage(language: LocalLanguage) {
        localSettingsStorage.updateLanguage(language)
    }

    override suspend fun updateTheme(theme: LocalTheme) {
        localSettingsStorage.updateTheme(theme)
    }

    override suspend fun updateTabsOrder(order: List<LocalTabs>) {
        localSettingsStorage.updateTabsOrder(order)
    }
}