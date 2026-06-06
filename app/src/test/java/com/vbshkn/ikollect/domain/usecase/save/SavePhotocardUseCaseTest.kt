package com.vbshkn.ikollect.domain.usecase.save

import com.vbshkn.ikollect.data.local.database.AppDatabase
import com.vbshkn.ikollect.domain.model.UserItemImage
import com.vbshkn.ikollect.domain.model.candidate.PhotocardCandidate
import com.vbshkn.ikollect.domain.repository.ImageRepository
import com.vbshkn.ikollect.domain.repository.PhotocardRepository
import com.vbshkn.ikollect.domain.repository.TagRepository
import com.vbshkn.ikollect.mockRoomTransactions
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SavePhotocardUseCaseTest {
    private val photocardRepository = mockk<PhotocardRepository>()
    private val tagRepository = mockk<TagRepository>()
    private val imageRepository = mockk<ImageRepository>()
    private val database = mockk<AppDatabase>()
    private val scope = TestScope()
    private val savePhotocardUseCase = SavePhotocardUseCase(
        photocardRepository,
        tagRepository,
        imageRepository,
        database,
        scope
    )

    @BeforeEach
    fun setUp() {
        mockRoomTransactions(database)
    }

    @Test
    fun `invoke should save cached covers to the internal storage`() = runTest {
        // Given
        val mockedImage = mockk<UserItemImage>(relaxed = true) {
            every { isCached } returns true
            every { uri } returns "content://cached_cover"
        }
        val candidate = mockk<PhotocardCandidate>(relaxed = true) {
            every { albumId } returns 1L
            every { image } returns mockedImage
            every { depictedArtistsId } returns listOf(1L, 2L, 3L)
            every { tagIds } returns setOf(6L, 7L, 8L)
        }

        coEvery { photocardRepository.insertWithArtists(any(), any()) } returns 1L
        coEvery { tagRepository.linkPhotocard(any(), any()) } just Runs
        coEvery { imageRepository.saveToInternalStorage(any()) } returns "internal://saved_cover"

        // When
        savePhotocardUseCase(candidate)
        scope.advanceUntilIdle()

        // Then
        coVerify(exactly = 1) {
            photocardRepository.insertWithArtists(
                entity = match { it.imageUrl == "internal://saved_cover" },
                artistIds = listOf(1L, 2L, 3L)
            )
        }
        coVerify(exactly = 1) {
            tagRepository.linkPhotocard(
                photocardId = 1L,
                tagIds = listOf(6L, 7L, 8L)
            )
        }
        coVerify(exactly = 1) {
            imageRepository.saveToInternalStorage("content://cached_cover")
        }
    }

    @Test
    fun `invoke should save albums with non-cached covers as they are`() = runTest {
        // Given
        val mockedImage = mockk<UserItemImage>(relaxed = true) {
            every { isCached } returns false
            every { uri } returns "internal://original_saved_cover"
        }
        val candidate = mockk<PhotocardCandidate>(relaxed = true) {
            every { albumId } returns 1L
            every { image } returns mockedImage
            every { depictedArtistsId } returns listOf(1L, 2L, 3L)
            every { tagIds } returns setOf(6L, 7L, 8L)
        }

        coEvery { photocardRepository.insertWithArtists(any(), any()) } returns 1L
        coEvery { tagRepository.linkPhotocard(any(), any()) } just Runs
        coEvery { imageRepository.saveToInternalStorage(any()) } returns "internal://saved_cover"

        // When
        savePhotocardUseCase(candidate)
        scope.advanceUntilIdle()

        // Then
        coVerify(exactly = 1) {
            photocardRepository.insertWithArtists(
                entity = match { it.imageUrl == "internal://original_saved_cover" },
                artistIds = listOf(1L, 2L, 3L)
            )
        }
        coVerify(exactly = 1) {
            tagRepository.linkPhotocard(
                photocardId = 1L,
                tagIds = listOf(6L, 7L, 8L)
            )
        }
        coVerify(exactly = 0) {
            imageRepository.saveToInternalStorage(any())
        }
    }
}