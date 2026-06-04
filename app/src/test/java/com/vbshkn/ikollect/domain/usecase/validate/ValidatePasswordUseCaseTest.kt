package com.vbshkn.ikollect.domain.usecase.validate

import com.vbshkn.ikollect.domain.error.ValidationError
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ValidatePasswordUseCaseTest {
    private val validatePasswordUseCase = ValidatePasswordUseCase()

    @Test
    fun `invoke return null for valid password`() {
        // Given
        val password = "valid123"

        // When
        val result = validatePasswordUseCase(password)

        // Then
        assertNull(result)
    }

    @Test
    fun `invoke return EmptyPassword error for blank password`() {
        // Given
        val password = ""

        // When
        val result = validatePasswordUseCase(password)

        // Then
        assertEquals(ValidationError.PasswordError.EmptyPassword, result)
    }

    @Test
    fun `invoke return InvalidPasswordFormat error for invalid password`() {
        // Given
        val password = "invalid"

        // When
        val result = validatePasswordUseCase(password)

        // Then
        assertEquals(ValidationError.PasswordError.InvalidPasswordFormat, result)
    }
}