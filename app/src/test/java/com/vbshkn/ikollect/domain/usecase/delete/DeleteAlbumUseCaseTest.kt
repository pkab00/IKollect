package com.vbshkn.ikollect.domain.usecase.delete

import com.vbshkn.ikollect.domain.repository.AlbumRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DeleteAlbumUseCaseTest {
    private val albumRepository = mockk<AlbumRepository>()
    private val deleteAlbumUseCase = DeleteAlbumUseCase(albumRepository)

    @Test
    fun `invoke should call softDelete on albumRepository with correct id`() = runTest {
        // Given
        val albumId = 123L
        coEvery { albumRepository.softDelete(any()) } just Runs

        // When
        deleteAlbumUseCase(albumId)

        // Then
        coVerify(exactly = 1) { albumRepository.softDelete(albumId) }
    }
}