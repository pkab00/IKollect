package com.vbshkn.ikollect.domain.usecase.get

import app.cash.turbine.test
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.profile.ArtistProfileData
import com.vbshkn.ikollect.domain.repository.ArtistRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetArtistProfileDataUseCaseTest {
    private val artistRepository = mockk<ArtistRepository>()
    private val getArtistProfileDataUseCase = GetArtistProfileDataUseCase(artistRepository)

    @Test
    fun `invoke should return exact same data by calling ArtistRepository`() = runTest {
        // Given
        val artistId = 1L
        val mockedData = mockk<ArtistProfileData>()
        val mockedResult = NetworkResult.Success(mockedData)
        coEvery { artistRepository.getArtistProfile(any()) } returns flowOf(mockedResult)

        // When
        val result = getArtistProfileDataUseCase(artistId)

        // Then
        result.test {
            val emittedResult = awaitItem()
            assertEquals(mockedResult, emittedResult)
            awaitComplete()
        }
        coVerify(exactly = 1) { artistRepository.getArtistProfile(artistId) }
    }
}