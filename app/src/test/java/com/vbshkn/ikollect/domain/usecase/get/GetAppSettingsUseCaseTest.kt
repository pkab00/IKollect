package com.vbshkn.ikollect.domain.usecase.get

import app.cash.turbine.test
import com.vbshkn.ikollect.domain.model.AppSettings
import com.vbshkn.ikollect.domain.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetAppSettingsUseCaseTest {
    private val settingsRepository = mockk<SettingsRepository>()
    private val getAppSettingsUseCase = GetAppSettingsUseCase(settingsRepository)

    @Test
    fun `invoke should return exact same data by calling SettingsRepository`() = runTest {
        // Given
        val mockedData = mockk<AppSettings>()
        coEvery { settingsRepository.getSettings() } returns flowOf(mockedData)

        // When
        val result = getAppSettingsUseCase()

        // Then
        result.test {
            val emittedData = awaitItem()
            assertSame(mockedData, emittedData)
            awaitComplete()
        }
        coVerify(exactly = 1) { settingsRepository.getSettings() }
    }
}