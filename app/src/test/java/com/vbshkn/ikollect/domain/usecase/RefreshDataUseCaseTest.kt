package com.vbshkn.ikollect.domain.usecase

import com.vbshkn.ikollect.data.background.HandshakeResult
import com.vbshkn.ikollect.data.background.SyncManager
import com.vbshkn.ikollect.domain.repository.AuthRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RefreshDataUseCaseTest {
    private val syncManager = mockk<SyncManager>(relaxed = true)
    private val authRepository = mockk<AuthRepository>(relaxed = true)
    private val refreshDataUseCase = RefreshDataUseCase(syncManager, authRepository)

    @Test
    fun `invoke should return true when online handshake runs successfully`() = runTest {
        // Given
        val userId = "testUserId"
        coEvery { authRepository.awaitAndGetUid() } returns userId
        coEvery { syncManager.performHandshake(any()) } returns HandshakeResult.FullSuccess
        coEvery { syncManager.offlineCheckup() } just Runs

        // When
        val result = refreshDataUseCase()

        // Then
        assertEquals(true, result)
        coVerify(exactly = 1) { authRepository.awaitAndGetUid() }
        coVerify(exactly = 1) { syncManager.performHandshake(userId) }
        coVerify(exactly = 0) { syncManager.offlineCheckup() }
    }

    @Test
    fun `invoke should return false when online handshake runs partially successfully`() = runTest {
        // Given
        val userId = "testUserId"
        coEvery { authRepository.awaitAndGetUid() } returns userId
        coEvery { syncManager.performHandshake(any()) } returns HandshakeResult.PartialSuccess
        coEvery { syncManager.offlineCheckup() } just Runs

        // When
        val result = refreshDataUseCase()

        // Then
        assertEquals(false, result)
        coVerify(exactly = 1) { authRepository.awaitAndGetUid() }
        coVerify(exactly = 1) { syncManager.performHandshake(userId) }
        coVerify(exactly = 0) { syncManager.offlineCheckup() }
    }

    @Test
    fun `invoke should return false when online handshake fails to run`() = runTest {
        // Given
        val userId = "testUserId"
        coEvery { authRepository.awaitAndGetUid() } returns userId
        coEvery { syncManager.performHandshake(any()) } returns HandshakeResult.Fail
        coEvery { syncManager.offlineCheckup() } just Runs

        // When
        val result = refreshDataUseCase()

        // Then
        assertEquals(false, result)
        coVerify(exactly = 1) { authRepository.awaitAndGetUid() }
        coVerify(exactly = 1) { syncManager.performHandshake(userId) }
        coVerify(exactly = 0) { syncManager.offlineCheckup() }
    }

    @Test
    fun `invoke should run offline checkup when no user given`() = runTest {
        // Given
        val userId = null
        coEvery { authRepository.awaitAndGetUid() } returns userId
        coEvery { syncManager.performHandshake(any()) } returns HandshakeResult.FullSuccess
        coEvery { syncManager.offlineCheckup() } just Runs

        // When
        val result = refreshDataUseCase()

        // Then
        assertEquals(true, result)
        coVerify(exactly = 1) { authRepository.awaitAndGetUid() }
        coVerify(exactly = 0) { syncManager.performHandshake(any()) }
        coVerify(exactly = 1) { syncManager.offlineCheckup() }
    }
}