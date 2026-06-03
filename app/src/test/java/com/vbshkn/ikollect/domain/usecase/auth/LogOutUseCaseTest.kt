package com.vbshkn.ikollect.domain.usecase.auth

import com.vbshkn.ikollect.data.local.database.AppDatabase
import com.vbshkn.ikollect.domain.repository.AuthRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LogOutUseCaseTest {
    private val authRepository = mockk<AuthRepository>()
    private val database = mockk<AppDatabase>()
    private val scope = TestScope()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `invoke should sign out and clean the database`() = runTest {
        // Given
        val logOutUseCase = LogOutUseCase(authRepository, database, scope)
        coEvery { authRepository.signOut() } just Runs
        every { database.clearAllTables() } just Runs

        // When
        logOutUseCase()
        scope.advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { authRepository.signOut() }
        verify(exactly = 1) { database.clearAllTables() }
    }
}