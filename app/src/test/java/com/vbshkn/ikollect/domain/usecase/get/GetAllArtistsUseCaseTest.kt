package com.vbshkn.ikollect.domain.usecase.get

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

class GetAllArtistsUseCaseTest {
    private val artistRepository = mockk<ArtistRepository>()
    private val getAllArtistsUseCase = GetAllArtistsUseCase(artistRepository)

    @Test
    fun `invoke should return exact same data by calling ArtistRepository`() = runTest {
        // Given
        val mockedList = listOf(mockk<ArtistListItem>())
        val mockedResult = NetworkResult.Success(mockedList)
        coEvery { artistRepository.getListItems() } returns flowOf(mockedResult)

        // When
        val result = getAllArtistsUseCase()

        // Then
        result.test {
            val emitted = awaitItem()
            assertEquals(NetworkResult.Success(mockedList), emitted)
            awaitComplete()
        }
        coVerify(exactly = 1) { artistRepository.getListItems() }
    }
}