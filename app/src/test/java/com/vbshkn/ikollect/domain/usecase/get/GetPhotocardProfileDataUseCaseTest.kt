package com.vbshkn.ikollect.domain.usecase.get

import app.cash.turbine.test
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.profile.PhotocardProfileData
import com.vbshkn.ikollect.domain.repository.PhotocardRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetPhotocardProfileDataUseCaseTest {
    private val photocardRepository = mockk<PhotocardRepository>()
    private val getPhotocardProfileDataUseCase = GetPhotocardProfileDataUseCase(photocardRepository)

    @Test
    fun `invoke should return exact same data by calling PhotocardRepository`() = runTest {
        // Given
        val photocardId = 1L
        val mockedData = mockk<PhotocardProfileData>()
        val mockedResult = NetworkResult.Success(mockedData)
        coEvery { photocardRepository.getPhotocardProfile(any()) } returns flowOf(mockedResult)

        // When
        val result = getPhotocardProfileDataUseCase(photocardId)

        // Then
        result.test {
            val emittedResult = awaitItem()
            assertEquals(mockedResult, emittedResult)
            awaitComplete()
        }
        coVerify(exactly = 1) { photocardRepository.getPhotocardProfile(photocardId) }
    }
}