package com.vbshkn.ikollect.domain.usecase.get

import app.cash.turbine.test
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.list.ArtistListItem
import com.vbshkn.ikollect.domain.repository.ArtistRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetGroupMembersUseCaseTest {
    private val artistRepository = mockk<ArtistRepository>()
    private val getGroupMembersUseCase = GetGroupMembersUseCase(artistRepository)

    @Test
    fun `invoke should return exact same data by calling ArtistRepository`() = runTest {
        // Given
        val groupId = 123L
        val mockedData = listOf(mockk<ArtistListItem>())
        val mockedResult = NetworkResult.Success(mockedData)
        coEvery { artistRepository.getGroupMembers(any()) } returns flowOf(mockedResult)

        // When
        val result = getGroupMembersUseCase(groupId)

        // Then
        result.test {
            val emittedResult = awaitItem()
            assertEquals(mockedResult, emittedResult)
            awaitComplete()
        }
        coVerify(exactly = 1) { artistRepository.getGroupMembers(groupId) }
    }

    @Test
    fun `invoke should return empty flow when null id given`() = runTest {
        // Given
        val expected = emptyFlow<NetworkResult<List<ArtistListItem>>>()

        // When
        val result = getGroupMembersUseCase(null)

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 0) { artistRepository.getGroupMembers(any()) }
    }
}