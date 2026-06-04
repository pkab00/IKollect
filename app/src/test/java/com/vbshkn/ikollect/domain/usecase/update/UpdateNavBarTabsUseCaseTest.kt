package com.vbshkn.ikollect.domain.usecase.update

import com.vbshkn.ikollect.data.mapper.toData
import com.vbshkn.ikollect.domain.repository.SettingsRepository
import com.vbshkn.ikollect.presentation.navigation.NavBarDestinations
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UpdateNavBarTabsUseCaseTest {
    private val settingsRepository = mockk<SettingsRepository>(relaxed = true)
    private val updateNavBarTabsUseCase = UpdateNavBarTabsUseCase(settingsRepository)

    @Test
    fun `invoke should call updateTabsOrder on settingsRepository`() = runTest {
        // Given
        val newTabs = listOf(
            NavBarDestinations.PHOTOCARDS,
            NavBarDestinations.ALBUMS,
            NavBarDestinations.ARTISTS,
            NavBarDestinations.PROFILE
        )

        // When
        updateNavBarTabsUseCase(newTabs)

        // Then
        coVerify(exactly = 1) { settingsRepository.updateTabsOrder(newTabs.map { it.toData() }) }
    }
}