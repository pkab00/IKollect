package com.vbshkn.ikollect.data.repository

import app.cash.turbine.test
import com.vbshkn.ikollect.data.local.datasource.ArtistLocalDataSource
import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.model.pojo.ArtistFullDetail
import com.vbshkn.ikollect.data.local.model.pojo.GroupWithMembers
import com.vbshkn.ikollect.data.mapper.DataMappers.toListItem
import com.vbshkn.ikollect.data.mapper.DataMappers.toProfile
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.data.remote.dao.ArtistDetailsResponse
import com.vbshkn.ikollect.data.remote.datasource.ArtistRemoteDataSource
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import retrofit2.Response

class ArtistRepositoryImplTest {
    private val artistLocalDS = mockk<ArtistLocalDataSource>(relaxed = true)
    private val artistRemoteDS = mockk<ArtistRemoteDataSource>(relaxed = true)
    private val repository = ArtistRepositoryImpl(artistLocalDS, artistRemoteDS)

    @Test
    fun `getGroupMembers should get and map the list of members`() = runTest {
        // Given
        val fakeId = 1L
        val fakeMembers = List(3) { mockk<ArtistEntity>(relaxed = true) }
        val fakeGroup = mockk<GroupWithMembers> {
            every { group.isGroup } returns true
            every { group.artistId } returns fakeId
            every { members } returns fakeMembers
        }
        coEvery { artistLocalDS.getGroupWithMembers(fakeId) } returns flowOf(fakeGroup)

        // When
        val result = repository.getGroupMembers(fakeId)

        // Then
        result.test {
            val loading = awaitItem()
            assertTrue(loading is NetworkResult.Loading)
            val success = awaitItem()
            assertTrue(success is NetworkResult.Success)
            assertEquals(3, (success as NetworkResult.Success).data.size)
            assertEquals(
                success.data,
                fakeGroup.members.map { it.toListItem() }
            )
            awaitComplete()
        }
    }

    @Test
    fun `getGroupMembers should return Success with empty data if no group found`() = runTest {
        // Given
        val fakeGroupId = 887L
        coEvery { artistLocalDS.getGroupWithMembers(fakeGroupId) } returns flowOf(null)

        // When
        val result = repository.getGroupMembers(fakeGroupId)

        // Then
        result.test {
            val loading = awaitItem()
            assertTrue(loading is NetworkResult.Loading)
            val success = awaitItem()
            assertTrue(success is NetworkResult.Success)
            assertTrue((success as NetworkResult.Success).data.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `getListItems should get and map the list of artists`() = runTest {
        // Given
        val fakeArtists = List(5) { mockk<ArtistEntity>(relaxed = true) }
        coEvery { artistLocalDS.getAll() } returns flowOf(fakeArtists)

        // When
        val result = repository.getListItems()

        // Then
        result.test {
            val loading = awaitItem()
            assertTrue(loading is NetworkResult.Loading)
            val success = awaitItem()
            assertTrue(success is NetworkResult.Success)
            assertEquals(5, (success as NetworkResult.Success).data.size)
            assertEquals(
                success.data,
                fakeArtists.map { it.toListItem() }
            )
            awaitComplete()
        }
    }

    @Test
    fun `getFavorite should get and map the list of favorite artists`() = runTest {
        // Given
        val fakeArtists = List(4) { mockk<ArtistEntity>(relaxed = true) }
        coEvery { artistLocalDS.getFavorite() } returns flowOf(fakeArtists)

        // When
        val result = repository.getFavorite()

        // Then
        result.test {
            val loading = awaitItem()
            assertTrue(loading is NetworkResult.Loading)
            val success = awaitItem()
            assertTrue(success is NetworkResult.Success)
            assertEquals(4, (success as NetworkResult.Success).data.size)
            assertEquals(
                success.data,
                fakeArtists.map { it.toListItem() }
            )
            awaitComplete()
        }
    }

    @Test
    fun `getArtistProfile should get and map the artist profile if artist exists`() = runTest {
        // Given
        val fakeId = 123L
        val fakeArtist = mockk<ArtistFullDetail>(relaxed = true)
        coEvery { artistLocalDS.getWithFullDetail(fakeId) } returns flowOf(fakeArtist)

        // When
        val result = repository.getArtistProfile(fakeId)

        // Then
        result.test {
            val loading = awaitItem()
            assertTrue(loading is NetworkResult.Loading)
            val success = awaitItem()
            assertTrue(success is NetworkResult.Success)
            assertEquals(
                (success as NetworkResult.Success).data,
                fakeArtist.toProfile()
            )
            awaitComplete()
        }
    }

    @Test
    fun `getArtistProfile should return Success with null data if artist not found`() = runTest {
        // Given
        val fakeId = 999L
        coEvery { artistLocalDS.getWithFullDetail(fakeId) } returns flowOf(null)

        // When
        val result = repository.getArtistProfile(fakeId)

        // Then
        result.test {
            val loading = awaitItem()
            assertTrue(loading is NetworkResult.Loading)
            val success = awaitItem()
            assertTrue(success is NetworkResult.Success)
            assertNull((success as NetworkResult.Success).data)
            awaitComplete()
        }
    }

    @Test
    fun `insertToDatabase should call insert on local datasource`() = runTest {
        // Given
        val fakeId = 123L
        val fakeEntity = mockk<ArtistEntity>(relaxed = true) {
            every { artistId } returns fakeId
        }

        // When
        repository.insertToDatabase(fakeEntity)

        // Then
        coVerify { artistLocalDS.insert(fakeEntity) }
    }

    @Test
    fun `addGroupMembers should insert and link group members when there are some`() = runTest {
        // Given
        val groupId = 1L
        val fakeGroup = mockk<ArtistEntity>(relaxed = true) {
            every { artistId } returns groupId
            every { isGroup } returns true
        }
        val memberIds = listOf(2L, 3L, 4L)
        val fakeMemberResponse = Response.success(mockk<ArtistDetailsResponse>(relaxed = true))
        coEvery { artistLocalDS.getById(groupId) } returns flowOf(fakeGroup)
        coEvery { artistRemoteDS.getArtistDetails(any()) } returns fakeMemberResponse
        coEvery { artistLocalDS.insert(any()) } just Runs

        // When
        repository.addGroupMembers(groupId, memberIds)

        // Then
        coVerify { artistRemoteDS.getArtistDetails(2L) }
        coVerify { artistRemoteDS.getArtistDetails(3L) }
        coVerify { artistRemoteDS.getArtistDetails(4L) }
        coVerify(exactly = 3) { artistLocalDS.insertAndLinkToGroup(any(), groupId)}
    }

    @Test
    fun `addGroupMembers should not insert and link group members when response body is empty`() = runTest {
        // Given
        val groupId = 1L
        val fakeGroup = mockk<ArtistEntity>(relaxed = true) {
            every { artistId } returns groupId
            every { isGroup } returns true
        }
        val memberIds = listOf(2L, 3L, 4L)
        val fakeMemberResponse = mockk<Response<ArtistDetailsResponse>>(relaxed = true) {
            every { isSuccessful } returns true
            every { body() } returns null
        }
        coEvery { artistLocalDS.getById(groupId) } returns flowOf(fakeGroup)
        coEvery { artistRemoteDS.getArtistDetails(any()) } returns fakeMemberResponse
        coEvery { artistLocalDS.insert(any()) } just Runs

        // When
        repository.addGroupMembers(groupId, memberIds)

        // Then
        coVerify { artistRemoteDS.getArtistDetails(2L) }
        coVerify { artistRemoteDS.getArtistDetails(3L) }
        coVerify { artistRemoteDS.getArtistDetails(4L) }
        coVerify(exactly = 0) { artistLocalDS.insertAndLinkToGroup(any(), groupId)}
    }

    @Test
    fun `addGroupMembers should not insert and link group members when response failed`() = runTest {
        // Given
        val groupId = 1L
        val fakeGroup = mockk<ArtistEntity>(relaxed = true) {
            every { artistId } returns groupId
            every { isGroup } returns true
        }
        val memberIds = listOf(2L, 3L, 4L)
        val fakeMemberResponse = mockk<Response<ArtistDetailsResponse>>(relaxed = true) {
            every { isSuccessful } returns false
            every { body() } returns null
        }
        coEvery { artistLocalDS.getById(groupId) } returns flowOf(fakeGroup)
        coEvery { artistRemoteDS.getArtistDetails(any()) } returns fakeMemberResponse
        coEvery { artistLocalDS.insert(any()) } just Runs

        // When
        repository.addGroupMembers(groupId, memberIds)

        // Then
        coVerify { artistRemoteDS.getArtistDetails(2L) }
        coVerify { artistRemoteDS.getArtistDetails(3L) }
        coVerify { artistRemoteDS.getArtistDetails(4L) }
        coVerify(exactly = 0) { artistLocalDS.insertAndLinkToGroup(any(), groupId)}
    }

    @Test
    fun `addGroupMembers should do nothing when the artist is not a group`() = runTest {
        // Given
        val groupId = 1L
        val fakeGroup = mockk<ArtistEntity>(relaxed = true) {
            every { artistId } returns groupId
            every { isGroup } returns false
        }
        val memberIds = listOf(2L, 3L, 4L)
        coEvery { artistLocalDS.getById(groupId) } returns flowOf(fakeGroup)

        // When
        repository.addGroupMembers(groupId, memberIds)

        // Then
        coVerify(exactly = 0) { artistRemoteDS.getArtistDetails(2L) }
        coVerify(exactly = 0) { artistRemoteDS.getArtistDetails(3L) }
        coVerify(exactly = 0) { artistRemoteDS.getArtistDetails(4L) }
        coVerify(exactly = 0) { artistLocalDS.insertAndLinkToGroup(any(), groupId)}
    }

    @Test
    fun `toggleFavorite should call setFavorite on local datasource when artist with given id exists`() = runTest {
        // Given
        val fakeId = 123L
        val fakeArtist = mockk<ArtistEntity>(relaxed = true) {
            every { artistId } returns fakeId
            every { isFavorite } returns true
        }
        coEvery { artistLocalDS.getById(fakeId) } returns flowOf(fakeArtist)
        coEvery { artistLocalDS.setFavorite(fakeId, any()) } just Runs

        // When
        repository.toggleFavorite(fakeId)

        // Then
        coVerify(exactly = 1) { artistLocalDS.setFavorite(fakeId, false) }
    }

    @Test
    fun `toggleFavorite should do nothing when artist with given id does not exist`() = runTest {
        // Given
        val fakeId = 999L
        coEvery { artistLocalDS.getById(fakeId) } returns flowOf(null)
        coEvery { artistLocalDS.setFavorite(fakeId, any()) } just Runs

        // When
        repository.toggleFavorite(fakeId)

        // Then
        coVerify(exactly = 0) { artistLocalDS.setFavorite(any(), any()) }
    }
}