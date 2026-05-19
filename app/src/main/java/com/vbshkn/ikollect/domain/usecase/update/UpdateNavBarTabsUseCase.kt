package com.vbshkn.ikollect.domain.usecase.update

import com.vbshkn.ikollect.data.mapper.toData
import com.vbshkn.ikollect.data.repository.SettingsRepository
import com.vbshkn.ikollect.presentation.navigation.NavBarDestinations
import javax.inject.Inject

class UpdateNavBarTabsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(newTabs: List<NavBarDestinations>) {
        settingsRepository.updateTabsOrder(newTabs.map { it.toData() })
    }
}