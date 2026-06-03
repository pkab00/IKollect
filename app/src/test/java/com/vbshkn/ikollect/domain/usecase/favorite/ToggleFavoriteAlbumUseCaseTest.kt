package com.vbshkn.ikollect.domain.usecase.favorite

import com.vbshkn.ikollect.domain.repository.AlbumRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ToggleFavoriteAlbumUseCaseTest {
    private val albumRepository = mockk<AlbumRepository>()
    private val toggleFavoriteAlbumUseCase = ToggleFavoriteAlbumUseCase(albumRepository)

    @Test
    fun `invoke should call toggleFavorite on repository with exact same id`() = runTest {
        // Given
        val albumId = 11L
        coEvery { albumRepository.toggleFavorite(any()) } just Runs

        // When
        toggleFavoriteAlbumUseCase(albumId)

        // Then
        coVerify(exactly = 1) { albumRepository.toggleFavorite(albumId) }
    }
}