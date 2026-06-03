package com.vbshkn.ikollect.domain.usecase.get

import app.cash.turbine.test
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.profile.AlbumProfileData
import com.vbshkn.ikollect.domain.repository.AlbumRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetAlbumProfileDataUseCaseTest {
    private val albumRepository = mockk<AlbumRepository>()
    private val getAlbumProfileDataUseCase = GetAlbumProfileDataUseCase(albumRepository)

    @Test
    fun `invoke should return exact same data by calling AlbumRepository`() = runTest {
        // Given
        val id = 1L
        val mockedAlbum = mockk<AlbumProfileData> { every { album.albumId } returns id }
        val mockedResult = NetworkResult.Success(mockedAlbum)
        coEvery { albumRepository.getAlbumProfile(id) } returns flowOf(mockedResult)

        // When
        val result = getAlbumProfileDataUseCase(id)

        // Then
        result.test {
            val emitted = awaitItem()
            assertEquals(mockedResult, emitted)
            awaitComplete()
        }
        coVerify(exactly = 1) { albumRepository.getAlbumProfile(id) }
    }
}