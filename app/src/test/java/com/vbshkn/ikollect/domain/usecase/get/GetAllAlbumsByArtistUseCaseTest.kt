package com.vbshkn.ikollect.domain.usecase.get

import app.cash.turbine.test
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.list.AlbumListItem
import com.vbshkn.ikollect.domain.repository.AlbumRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetAllAlbumsByArtistUseCaseTest {
    private val albumRepository = mockk<AlbumRepository>()
    private val getAllAlbumsUseCase = GetAllAlbumsByArtistUseCase(albumRepository)

    @Test
    fun `invoke should return exact same data by calling AlbumRepository`() = runTest {
        // Given
        val artistId = 1L
        val mockedList = listOf(mockk<AlbumListItem>())
        val mockedResult = NetworkResult.Success(mockedList)
        every { albumRepository.getListItemsByArtist(any()) } returns flowOf(mockedResult)

        // When
        val result = getAllAlbumsUseCase(artistId)

        // Then
        result.test {
            val emitted = awaitItem()
            assertEquals(mockedResult, emitted)
            awaitComplete()
        }
        coVerify(exactly = 1) { albumRepository.getListItemsByArtist(artistId) }
    }
}