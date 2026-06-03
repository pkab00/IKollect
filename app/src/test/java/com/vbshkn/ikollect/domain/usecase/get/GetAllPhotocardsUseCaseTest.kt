package com.vbshkn.ikollect.domain.usecase.get

import app.cash.turbine.test
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.list.PhotocardListItem
import com.vbshkn.ikollect.domain.repository.PhotocardRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetAllPhotocardsUseCaseTest {
    private val photocardRepository = mockk<PhotocardRepository>()
    private val getAllPhotocardsUseCase = GetAllPhotocardsUseCase(photocardRepository)

    @Test
    fun `invoke should return exact same data by calling PhotocardsRepository`() = runTest {
        // Given
        val mockedList = listOf(mockk<PhotocardListItem>())
        val mockedResult = NetworkResult.Success(mockedList)
        coEvery { photocardRepository.getAll() } returns flowOf(mockedResult)

        // When
        val result = getAllPhotocardsUseCase()

        // Then
        result.test {
            val emitted = awaitItem()
            assertEquals(mockedResult, emitted)
            awaitComplete()
        }
        coVerify(exactly = 1) { photocardRepository.getAll() }
    }
}