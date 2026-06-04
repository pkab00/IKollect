package com.vbshkn.ikollect.domain.usecase.update

import com.vbshkn.ikollect.domain.repository.AuthRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UpdateUserNicknameUseCaseTest {
    private val authRepository = mockk<AuthRepository>(relaxed = true)
    private val updateUserNicknameUseCase = UpdateUserNicknameUseCase(authRepository)

    @Test
    fun `invoke should call changeNickname on authRepository`() = runTest {
        // Given
        val newNickname = "NewNickname"

        // When
        updateUserNicknameUseCase(newNickname)

        // Then
        coVerify(exactly = 1) { authRepository.changeNickname(newNickname) }
    }
}