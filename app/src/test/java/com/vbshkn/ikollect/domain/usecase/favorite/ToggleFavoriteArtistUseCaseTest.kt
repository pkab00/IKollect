package com.vbshkn.ikollect.domain.usecase.favorite

import com.vbshkn.ikollect.domain.repository.ArtistRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ToggleFavoriteArtistUseCaseTest {
    private val artistRepository = mockk<ArtistRepository>()
    private val toggleFavoriteArtistUseCase = ToggleFavoriteArtistUseCase(artistRepository)

    @Test
    fun `invoke should call toggleFavorite on repository with exact same id`() = runTest {
        // Given
        val artistId = 11L
        coEvery { artistRepository.toggleFavorite(any()) } just Runs

        // When
        toggleFavoriteArtistUseCase(artistId)

        // Then
        coVerify(exactly = 1) { artistRepository.toggleFavorite(artistId) }
    }
}