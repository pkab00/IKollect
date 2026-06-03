package com.vbshkn.ikollect.domain.usecase.favorite

import app.cash.turbine.test
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.details.AlbumDetails
import com.vbshkn.ikollect.domain.repository.AlbumRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetFavoriteAlbumsUseCaseTest {
    private val albumRepository = mockk<AlbumRepository>()
    private val getFavoriteAlbumsUseCase = GetFavoriteAlbumsUseCase(albumRepository)

    @Test
    fun `invoke should call albumRepository getFavoriteAlbums`() = runTest {
        // Given
        val mockedAlbums = listOf(mockk<AlbumDetails>())
        val mockedResult = NetworkResult.Success(mockedAlbums)
        coEvery { albumRepository.getFavoriteAlbums() } returns flowOf(mockedResult)

        // When
        val result = getFavoriteAlbumsUseCase()

        // Then
        result.test {
            val emitted = awaitItem()
            assertEquals(mockedResult, emitted)
            awaitComplete()
        }
        coVerify(exactly = 1) { albumRepository.getFavoriteAlbums() }
    }
}