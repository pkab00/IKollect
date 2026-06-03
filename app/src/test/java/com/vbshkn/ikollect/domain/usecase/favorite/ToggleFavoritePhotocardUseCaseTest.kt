package com.vbshkn.ikollect.domain.usecase.favorite

import com.vbshkn.ikollect.domain.repository.PhotocardRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ToggleFavoritePhotocardUseCaseTest {
    private val photocardRepository = mockk<PhotocardRepository>()
    private val toggleFavoritePhotocardUseCase = ToggleFavoritePhotocardUseCase(photocardRepository)

    @Test
    fun `invoke should call toggleFavorite on repository with exact same id`() = runTest {
        // Given
        val photocardId = 11L
        coEvery { photocardRepository.toggleFavorite(any()) } just Runs

        // When
        toggleFavoritePhotocardUseCase(photocardId)

        // Then
        coVerify(exactly = 1) { photocardRepository.toggleFavorite(photocardId) }
    }
}