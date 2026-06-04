package com.vbshkn.ikollect.domain.usecase.update

import com.vbshkn.ikollect.data.local.datastore.LocalTheme
import com.vbshkn.ikollect.domain.repository.SettingsRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UpdateAppThemeUseCaseTest {
    private val settingsRepository = mockk<SettingsRepository>(relaxed = true)
    private val updateAppThemeUseCase = UpdateAppThemeUseCase(settingsRepository)

    @Test
    fun `invoke should call updateTheme on settingsRepository`() = runTest {
        // Given
        val theme = LocalTheme.DARK
        coEvery { settingsRepository.updateTheme(theme) } just Runs

        // When
        updateAppThemeUseCase(theme)

        // Then
        coVerify(exactly = 1) { settingsRepository.updateTheme(theme) }
    }
}