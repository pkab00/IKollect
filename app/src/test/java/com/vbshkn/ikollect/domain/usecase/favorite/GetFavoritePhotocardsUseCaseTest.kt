package com.vbshkn.ikollect.domain.usecase.favorite

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

class GetFavoritePhotocardsUseCaseTest {
    val photocardRepository = mockk<PhotocardRepository>()
    val getFavoritePhotocardsUseCase = GetFavoritePhotocardsUseCase(photocardRepository)

    @Test
    fun `invoke should call getFavorite on photocardRepository`() = runTest {
        // Given
        val mockedPhotocards = listOf(mockk<PhotocardListItem>())
        val mockedResult = NetworkResult.Success(mockedPhotocards)
        coEvery { photocardRepository.getFavorite() } returns flowOf(mockedResult)

        // When
        val result = getFavoritePhotocardsUseCase()

        // Then
        result.test {
            val emitted = awaitItem()
            assertEquals(mockedResult, emitted)
            awaitComplete()
        }
        coVerify(exactly = 1) { photocardRepository.getFavorite() }
    }
}