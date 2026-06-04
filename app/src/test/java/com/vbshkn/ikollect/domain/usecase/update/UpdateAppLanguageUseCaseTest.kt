package com.vbshkn.ikollect.domain.usecase.update

import com.vbshkn.ikollect.data.local.datastore.LocalLanguage
import com.vbshkn.ikollect.domain.repository.SettingsRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UpdateAppLanguageUseCaseTest {
    private val settingsRepository = mockk<SettingsRepository>(relaxed = true)
    private val updateAppLanguageUseCase = UpdateAppLanguageUseCase(settingsRepository)

    @Test
    fun `invoke should call updateLanguage on settingsRepository`() = runTest {
        // Given
        val language = LocalLanguage.ENGLISH
        coEvery { settingsRepository.updateLanguage(language) } just Runs

        // When
        updateAppLanguageUseCase(language)

        // Then
        coVerify { settingsRepository.updateLanguage(language) }
    }
}