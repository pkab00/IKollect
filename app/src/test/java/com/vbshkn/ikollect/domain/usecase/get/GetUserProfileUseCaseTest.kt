package com.vbshkn.ikollect.domain.usecase.get

import app.cash.turbine.test
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.AppUser
import com.vbshkn.ikollect.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetUserProfileUseCaseTest {
    private val authRepository = mockk<AuthRepository>()
    private val getUserProfileUseCase = GetUserProfileUseCase(authRepository)

    @Test
    fun `invoke should return exact same data by calling AuthRepository`() = runTest {
        // Given
        val mockedData = mockk<AppUser>()
        val mockedResult = NetworkResult.Success(mockedData)
        coEvery { authRepository.getUser() } returns flowOf(mockedResult)

        // When
        val result = getUserProfileUseCase()

        // Then
        result.test {
            val emitted = awaitItem()
            assertEquals(mockedResult, emitted)
            awaitComplete()
        }
        coVerify(exactly = 1) { authRepository.getUser() }
    }
}