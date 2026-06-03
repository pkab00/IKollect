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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.net.ConnectException

class RegisterUserUseCaseTest {
    private val authRepository = mockk<AuthRepository>()
    private val registerUserUseCase = RegisterUserUseCase(authRepository)
    private val login = "test@gmail.com"
    private val password = "password123"
    private val nickname = "testuser"

    @Test
    fun `invoke should return null when successfully registered`() = runTest {
        // Given
        coEvery { authRepository.createUser(any(), any(), any()) } just Runs

        // When
        val result = registerUserUseCase(login, password, nickname)

        // Then
        assertEquals(null, result)
        coVerify(exactly = 1) { authRepository.createUser(login, password, nickname) }
    }

    @Test
    fun `invoke should map the server exception when the user already exists`() = runTest {
        val exception = AuthRestException(
            errorCode = "user_already_exists",
            errorDescription = "",
            response = mockk(relaxed = true)
        )
        coEvery { authRepository.createUser(any(), any(), any()) } throws exception

        // When
        val result = registerUserUseCase(login, password, nickname)
        val expected = UserAuthError.Registration.EmailAlreadyInUse

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 1) { authRepository.createUser(login, password, nickname) }
    }

    @Test
    fun `invoke should map the server exception when the password is weak`() = runTest {
        val exception = AuthRestException(
            errorCode = "weak_password",
            errorDescription = "",
            response = mockk(relaxed = true)
        )
        coEvery { authRepository.createUser(any(), any(), any()) } throws exception

        // When
        val result = registerUserUseCase(login, password, nickname)
        val expected = UserAuthError.Registration.UnknownError("Weak password.")

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 1) { authRepository.createUser(login, password, nickname) }
    }

    @Test
    fun `invoke should map the server exception when the rate limit is over`() = runTest {
        val exception = AuthRestException(
            errorCode = "over_email_send_rate_limit",
            errorDescription = "",
            response = mockk(relaxed = true)
        )
        coEvery { authRepository.createUser(any(), any(), any()) } throws exception

        // When
        val result = registerUserUseCase(login, password, nickname)
        val expected = UserAuthError.Registration.UnknownError("Too many attempts. Try again later.")

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 1) { authRepository.createUser(login, password, nickname) }
    }

    @Test
    fun `invoke should handle unknown registration errors from server`() = runTest {
        val exception = AuthRestException(
            errorCode = "some_unknown_error_code",
            errorDescription = "",
            response = mockk(relaxed = true)
        )
        coEvery { authRepository.createUser(any(), any(), any()) } throws exception

        // When
        val result = registerUserUseCase(login, password, nickname)
        val expected = UserAuthError.Registration.UnknownError(exception.description ?: "Registration error.")

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 1) { authRepository.createUser(login, password, nickname) }
    }

    @Test
    fun `invoke should handle connection errors`() = runTest {
        val exception = ConnectException("Failed to connect to server")
        coEvery { authRepository.createUser(any(), any(), any()) } throws exception

        // When
        val result = registerUserUseCase(login, password, nickname)
        val expected = UserAuthError.Registration.UnknownError("Server connection error.")

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 1) { authRepository.createUser(login, password, nickname) }
    }

    @Test
    fun `invoke should handle unknown errors`() = runTest {
        val exception = Exception()
        coEvery { authRepository.createUser(any(), any(), any()) } throws exception

        // When
        val result = registerUserUseCase(login, password, nickname)
        val expected = UserAuthError.Registration.UnknownError(exception.localizedMessage ?: "Unknown error.")

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 1) { authRepository.createUser(login, password, nickname) }
    }
}