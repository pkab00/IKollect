package com.vbshkn.ikollect.domain.usecase.validate

import com.vbshkn.ikollect.domain.error.ValidationError
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ValidateEmailUseCaseTest {
    private val validateEmailUseCase = ValidateEmailUseCase()

    @Test
    fun `invoke return null for valid email`() {
        // Given
        val email = "valid@example.com"

        // When
        val result = validateEmailUseCase(email)

        // Then
        assertNull(result)
    }

    @Test
    fun `invoke return EmptyEmail error for blank email`() {
        // Given
        val email = ""

        // When
        val result = validateEmailUseCase(email)

        // Then
        assertEquals(ValidationError.EmailError.EmptyEmail, result)
    }

    @Test
    fun `invoke return InvalidEmailFormat error for invalid email`() {
        // Given
        val email = "invalid-email"

        // When
        val result = validateEmailUseCase(email)

        // Then
        assertEquals(ValidationError.EmailError.InvalidEmailFormat, result)
    }
}