package com.vbshkn.ikollect.domain.usecase.get

import app.cash.turbine.test
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.TagItem
import com.vbshkn.ikollect.domain.repository.TagRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetAllTagsUseCaseTest {
    private val tagRepository = mockk<TagRepository>()
    private val getAllTagsUseCase = GetAllTagsUseCase(tagRepository)

    @Test
    fun `invoke should return exact same data by calling TagRepository`() = runTest {
        // Given
        val mockedList = listOf(mockk<TagItem>())
        val mockedResult = NetworkResult.Success(mockedList)
        coEvery { tagRepository.getAll() } returns flowOf(mockedResult)

        // When
        val result = getAllTagsUseCase()

        // Then
        result.test {
            val emitted = awaitItem()
            assertEquals(mockedResult, emitted)
            awaitComplete()
        }
        coVerify(exactly = 1) { tagRepository.getAll() }
    }
}