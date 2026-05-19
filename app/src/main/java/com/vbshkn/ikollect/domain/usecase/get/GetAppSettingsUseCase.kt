package com.vbshkn.ikollect.domain.usecase.get

import com.vbshkn.ikollect.data.repository.SettingsRepository
import javax.inject.Inject

class GetAppSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke() = settingsRepository.getSettings()
}