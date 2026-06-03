package com.vbshkn.ikollect.domain.usecase.favorite

import app.cash.turbine.test
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.list.ArtistListItem
import com.vbshkn.ikollect.domain.repository.ArtistRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetFavoriteArtistsUseCaseTest {
    private val artistRepository = mockk<ArtistRepository>()
    private val getFavoriteArtistsUseCase = GetFavoriteArtistsUseCase(artistRepository)

    @Test
    fun `invoke should call getFavorite on artistRepository`() = runTest {
        // Given
        val mockedArtists = listOf(mockk<ArtistListItem>())
        val mockedResult = NetworkResult.Success(mockedArtists)
        coEvery { artistRepository.getFavorite() } returns flowOf(mockedResult)

        // When
        val result = getFavoriteArtistsUseCase()

        // Then
        result.test {
            val emitted = awaitItem()
            assertEquals(mockedResult, emitted)
            awaitComplete()
        }
        coVerify(exactly = 1) { artistRepository.getFavorite() }
    }
}