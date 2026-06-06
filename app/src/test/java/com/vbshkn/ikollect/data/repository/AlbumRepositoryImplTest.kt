package com.vbshkn.ikollect.data.repository

import app.cash.turbine.test
import com.vbshkn.ikollect.data.local.datasource.AlbumLocalDataSource
import com.vbshkn.ikollect.data.local.model.entity.AlbumEntity
import com.vbshkn.ikollect.data.local.model.pojo.AlbumFullDetail
import com.vbshkn.ikollect.data.local.model.pojo.AlbumWithArtists
import com.vbshkn.ikollect.data.local.model.pojo.ArtistWithAlbums
import com.vbshkn.ikollect.data.mapper.DataMappers.toCandidate
import com.vbshkn.ikollect.data.mapper.DataMappers.toDetails
import com.vbshkn.ikollect.data.mapper.DataMappers.toListItem
import com.vbshkn.ikollect.data.mapper.DataMappers.toProfile
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.data.remote.dao.FullReleaseData
import com.vbshkn.ikollect.data.remote.dao.ReleaseDetailsResponse
import com.vbshkn.ikollect.data.remote.datasource.AlbumRemoteDataSource
import com.vbshkn.ikollect.domain.error.AppError
import com.vbshkn.ikollect.domain.model.candidate.AlbumCandidate
import com.vbshkn.ikollect.domain.model.profile.AlbumProfileData
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.collections.map

class AlbumRepositoryImplTest {
    private val albumRemoteDS = mockk<AlbumRemoteDataSource>(relaxed = true)
    private val albumLocalDS = mockk<AlbumLocalDataSource>(relaxed = true)
    private val repository = AlbumRepositoryImpl(albumRemoteDS, albumLocalDS)
    private val testBarcode = "1234567890"

    @Test
    fun `getAlbumCandidate returns loading and then success when valid styles are present`() = runTest {
        // Given
        val fakeRemoteData = mockk<FullReleaseData>(relaxed = true)
        every { fakeRemoteData.releaseDetailsResponse.styles } returns listOf("Pop", "K-Pop")
        coEvery { albumRemoteDS.getFullReleaseData(testBarcode) } returns NetworkResult.Success(fakeRemoteData)

        // When
        val result = repository.getAlbumCandidate(testBarcode)

        // Then
        result.test {
            val loading = awaitItem()
            assertTrue(loading is NetworkResult.Loading)
            val success = awaitItem()
            assertTrue(success is NetworkResult.Success)
            awaitComplete()
        }
    }

    @Test
    fun `getAlbumCandidate returns loading and then error when invalid styles are present`() = runTest {
        // Given
        val fakeRemoteData = mockk<FullReleaseData>(relaxed = true)
        every { fakeRemoteData.releaseDetailsResponse.styles } returns listOf("Pop", "Latino")
        coEvery { albumRemoteDS.getFullReleaseData(testBarcode) } returns NetworkResult.Success(fakeRemoteData)

        // When
        val result = repository.getAlbumCandidate(testBarcode)

        // Then
        result.test {
            val loading = awaitItem()
            assertTrue(loading is NetworkResult.Loading)
            val error = awaitItem()
            assertTrue(error is NetworkResult.Error)
            assertEquals(AppError.InvalidAlbumStyle, (error as NetworkResult.Error).error)
            awaitComplete()
        }
    }

    @Test
    fun `getAlbumCandidate returns loading and then error when remote datasource returns an error`() = runTest {
        // Given
        coEvery { albumRemoteDS.getFullReleaseData(testBarcode) } returns NetworkResult.Error(AppError.ReleaseNotFound)

        // When
        val result = repository.getAlbumCandidate(testBarcode)

        // Then
        result.test {
            val loading = awaitItem()
            assertTrue(loading is NetworkResult.Loading)
            val error = awaitItem()
            assertTrue(error is NetworkResult.Error)
            assertEquals(AppError.ReleaseNotFound, (error as NetworkResult.Error).error)
            awaitComplete()
        }
    }

    @Test
    fun `getEntity should call getById on local datasource`() = runTest {
        // Given
        val fakeAlbumId = 1L
        val fakeAlbumEntity = mockk<AlbumEntity> {
            every { albumId } returns fakeAlbumId
        }
        coEvery { albumLocalDS.getById(fakeAlbumId) } returns flowOf(fakeAlbumEntity)

        // When
        val result = repository.getEntity(fakeAlbumId).first()

        // Then
        assertTrue { result != null }
        assertEquals(fakeAlbumId, result?.albumId)
        coVerify { albumLocalDS.getById(fakeAlbumId) }
    }

    @Test
    fun `getEntity should return null if no album with such id found`() = runTest {
        // Given
        val fakeAlbumId = 999L
        coEvery { albumLocalDS.getById(fakeAlbumId) } returns flowOf()

        // When
        val result = repository.getEntity(fakeAlbumId).first()

        // Then
        assertTrue { result == null }
    }

    @Test
    fun `getAllDetails should get details and map them to domain`() = runTest {
        // Given
        val fakeAlbumEntities = listOf(mockk<AlbumWithArtists>(relaxed = true))
        coEvery { albumLocalDS.getAllWithArtists() } returns flowOf(fakeAlbumEntities)

        // When
        val result = repository.getAllDetails()

        // Then
        result.test {
            val loading = awaitItem()
            assertTrue { loading is NetworkResult.Loading }
            val success = awaitItem()
            assertTrue { success is NetworkResult.Success }
            assertEquals(fakeAlbumEntities.size, (success as NetworkResult.Success).data.size)
            assertEquals(
                NetworkResult.Success(fakeAlbumEntities.map { it.toDetails() }),
                success
            )
            awaitComplete()
        }
    }

    @Test
    fun `getListItems should get details and map them to domain`() = runTest {
        // Given
        val fakeAlbumEntities = listOf(mockk<AlbumEntity>(relaxed = true))
        coEvery { albumLocalDS.getAll() } returns flowOf(fakeAlbumEntities)

        // When
        val result = repository.getListItems()

        // Then
        result.test {
            val loading = awaitItem()
            assertTrue { loading is NetworkResult.Loading }
            val success = awaitItem()
            assertTrue { success is NetworkResult.Success }
            assertEquals(fakeAlbumEntities.size, (success as NetworkResult.Success).data.size)
            assertEquals(
                NetworkResult.Success(fakeAlbumEntities.map { it.toListItem() }),
                success
            )
            awaitComplete()
        }
    }

    @Test
    fun `getFavoriteAlbums should get details and map them to domain`() = runTest {
        // Given
        val fakeAlbumEntities = listOf(mockk<AlbumWithArtists>(relaxed = true))
        coEvery { albumLocalDS.getFavoriteWithArtists() } returns flowOf(fakeAlbumEntities)

        // When
        val result = repository.getFavoriteAlbums()

        // Then
        result.test {
            val loading = awaitItem()
            assertTrue { loading is NetworkResult.Loading }
            val success = awaitItem()
            assertTrue { success is NetworkResult.Success }
            assertEquals(fakeAlbumEntities.size, (success as NetworkResult.Success).data.size)
            assertEquals(
                NetworkResult.Success(fakeAlbumEntities.map { it.toDetails() }),
                success
            )
            awaitComplete()
        }
    }

    @Test
    fun `getListItemsByArtist should get details and map them to domain`() = runTest {
        // Given
        val fakeArtistId = 1L
        val fakeAlbumEntities = listOf(
            mockk<ArtistWithAlbums>(relaxed = true) {
                every { albums } returns listOf(mockk(relaxed = true))
            }
        )
        coEvery { albumLocalDS.getAllByArtist(fakeArtistId) } returns flowOf(fakeAlbumEntities)

        // When
        val result = repository.getListItemsByArtist(fakeArtistId)

        // Then
        result.test {
            val loading = awaitItem()
            assertTrue { loading is NetworkResult.Loading }
            val success = awaitItem()
            assertTrue { success is NetworkResult.Success }
            assertEquals(fakeAlbumEntities.size, (success as NetworkResult.Success).data.size)
            assertEquals(
                NetworkResult.Success(fakeAlbumEntities.flatMap { it.albums }.map { it.toListItem() }),
                success
            )
            awaitComplete()
        }
        coVerify { albumLocalDS.getAllByArtist(fakeArtistId) }
    }

    @Test
    fun `getAlbumProfile should get details and map them to domain`() = runTest {
        // Given
        val fakeId = 1L
        val fakeAlbumEntity = mockk<AlbumFullDetail>(relaxed = true)
        coEvery { albumLocalDS.getWithFullDetail(fakeId) } returns flowOf(fakeAlbumEntity)

        // When
        val result = repository.getAlbumProfile(fakeId)

        // Then
        result.test {
            val loading = awaitItem()
            assertTrue { loading is NetworkResult.Loading }
            val success = awaitItem()
            assertTrue { success is NetworkResult.Success }
            assertEquals(
                NetworkResult.Success(fakeAlbumEntity.toProfile()),
                success
            )
            coVerify { albumLocalDS.getWithFullDetail(fakeId) }
            awaitComplete()
        }
    }

    @Test
    fun `insertToDatabase should call insertAlbumWithArtists on local datasource`() = runTest {
        // Given
        val fakeAlbumEntity = mockk<AlbumEntity>(relaxed = true)
        val fakeArtistIds = listOf(1L, 2L, 3L)
        coEvery { albumLocalDS.insertAlbumWithArtists(fakeAlbumEntity, fakeArtistIds) } returns 1L

        // When
        repository.insertToDatabase(fakeAlbumEntity, fakeArtistIds)

        // Then
        coVerify(exactly = 1) { albumLocalDS.insertAlbumWithArtists(fakeAlbumEntity, fakeArtistIds) }
    }

    @Test
    fun `updateAlbum should call updateAlbum on local datasource`() = runTest {
        // Given
        val fakeAlbumEntity = mockk<AlbumEntity>(relaxed = true)
        coEvery { albumLocalDS.updateAlbum(any()) } just Runs

        // When
        repository.updateAlbum(fakeAlbumEntity)

        // Then
        coVerify(exactly = 1) { albumLocalDS.updateAlbum(fakeAlbumEntity) }
    }

    @Test
    fun `toggleFavorite should toggle favorite state if the album entity exists`() = runTest {
        // Given
        val fakeId = 1L
        val fakeAlbumEntity = mockk<AlbumEntity>(relaxed = true) {
            every { albumId } returns fakeId
            every { isFavorite } returns false
        }
        coEvery { albumLocalDS.getById(fakeId) } returns flowOf(fakeAlbumEntity)

        // When
        repository.toggleFavorite(fakeId)

        // Then
        coVerify(exactly = 1) { albumLocalDS.setFavorite(fakeId, true) }
    }

    @Test
    fun `toggleFavorite should not do anything if the album entity does not exist`() = runTest {
        // Given
        val fakeId = 999L
        coEvery { albumLocalDS.getById(fakeId) } returns flowOf(null)

        // When
        repository.toggleFavorite(fakeId)

        // Then
        coVerify(exactly = 0) { albumLocalDS.setFavorite(any(), any()) }
    }

    @Test
    fun `softDelete should call setDeleted on local datasource`() = runTest {
        // Given
        val fakeId = 1L
        coEvery { albumLocalDS.setDeleted(fakeId) } just Runs

        // When
        repository.softDelete(fakeId)

        // Then
        coVerify(exactly = 1) { albumLocalDS.setDeleted(fakeId) }
    }
}