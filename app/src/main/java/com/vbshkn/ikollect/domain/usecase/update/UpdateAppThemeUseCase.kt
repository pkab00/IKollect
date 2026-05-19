package com.vbshkn.ikollect.domain.usecase.update

import com.vbshkn.ikollect.data.local.datastore.LocalTheme
import com.vbshkn.ikollect.data.repository.SettingsRepository
import javax.inject.Inject

class UpdateAppThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(theme: LocalTheme) {
        settingsRepository.updateTheme(theme)
    }
}