package com.vbshkn.ikollect.domain.usecase.validate

import com.vbshkn.ikollect.domain.error.ValidationError
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ValidateNicknameUseCaseTest {
    private val validateNicknameUseCase = ValidateNicknameUseCase()

    @Test
    fun `invoke return null for valid nickname`() {
        // Given
        val nickname = "valid_nickname"

        // When
        val result = validateNicknameUseCase(nickname)

        // Then
        assertNull(result)
    }

    @Test
    fun `invoke return EmptyNickname error for blank nickname`() {
        // Given
        val nickname = ""

        // When
        val result = validateNicknameUseCase(nickname)

        // Then
        assertEquals(ValidationError.NicknameError.EmptyNickname, result)
    }

    @Test
    fun `invoke return InvalidNicknameFormat error for invalid nickname`() {
        // Given
        val nickname = "invalid nickname!"

        // When
        val result = validateNicknameUseCase(nickname)

        // Then
        assertEquals(ValidationError.NicknameError.InvalidNicknameFormat, result)
    }
}