package com.vbshkn.ikollect.domain.usecase.delete

import com.vbshkn.ikollect.domain.repository.PhotocardRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DeletePhotocardUseCaseTest {
    private val photocardRepository = mockk<PhotocardRepository>()
    private val deletePhotocardUseCase = DeletePhotocardUseCase(photocardRepository)

    @Test
    fun `invoke should call softDelete on photocardRepository with correct id`() = runTest {
        // Given
        val photocardId = 123L
        coEvery { photocardRepository.softDelete(any()) } just Runs

        // When
        deletePhotocardUseCase(photocardId)

        // Then
        coVerify(exactly = 1) { photocardRepository.softDelete(photocardId) }
    }
}