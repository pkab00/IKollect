package com.vbshkn.ikollect.domain.usecase.auth

import com.vbshkn.ikollect.domain.error.UserAuthError
import com.vbshkn.ikollect.domain.repository.AuthRepository
import io.github.jan.supabase.auth.exception.AuthRestException
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LogInUseCaseTest {
    private val authRepository = mockk<AuthRepository>()
    private val logInUseCase = LogInUseCase(authRepository)

    @Test
    fun `invoke should return null when sign in is successful`() = runTest {
        // Given
        val email = "test.email@gmail.com"
        val password = "password123"
        coEvery { authRepository.signInWithEmail(any(), any()) } just Runs

        // When
        val result = logInUseCase(email, password)

        // Then
        assertEquals(null, result)
        coVerify(exactly = 1) { authRepository.signInWithEmail(email, password) }
    }

    @Test
    fun `invoke should return InvalidUser error when the repository throws AuthRestException`() = runTest {
        // Given
        val email = "test.email@gmail.com"
        val password = "password123"
        val exception = AuthRestException(
            errorCode = "code",
            errorDescription = "description",
            response = mockk(relaxed = true)
        )
        val expectedError = UserAuthError.Login.InvalidUser
        coEvery { authRepository.signInWithEmail(any(), any()) } throws exception

        // When
        val result = logInUseCase(email, password)

        // Then
        assertEquals(expectedError, result)
        coVerify(exactly = 1) { authRepository.signInWithEmail(email, password) }
    }

    @Test
    fun `invoke should return UnknownError when the repository throws an unexpected exception`() = runTest {
        // Given
        val email = "test.email@gmail.com"
        val password = "password123"
        val exception = Exception("Unexpected error")
        val expectedError = UserAuthError.Login.UnknownError("Unexpected error")
        coEvery { authRepository.signInWithEmail(any(), any()) } throws exception

        // When
        val result = logInUseCase(email, password)

        // Then
        assertEquals(expectedError, result)
        coVerify(exactly = 1) { authRepository.signInWithEmail(email, password) }
    }
}