package com.vbshkn.ikollect.domain.usecase.update

import com.vbshkn.ikollect.data.local.datastore.LocalLanguage
import com.vbshkn.ikollect.data.repository.SettingsRepositoryImpl
import com.vbshkn.ikollect.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateAppLanguageUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(language: LocalLanguage) = settingsRepository.updateLanguage(language)
}