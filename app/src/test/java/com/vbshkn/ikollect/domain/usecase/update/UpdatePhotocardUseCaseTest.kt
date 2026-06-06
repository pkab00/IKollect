package com.vbshkn.ikollect.domain.usecase.update

import com.vbshkn.ikollect.data.local.database.AppDatabase
import com.vbshkn.ikollect.data.local.model.entity.PhotocardEntity
import com.vbshkn.ikollect.domain.model.UserItemImage
import com.vbshkn.ikollect.domain.repository.ImageRepository
import com.vbshkn.ikollect.domain.repository.PhotocardRepository
import com.vbshkn.ikollect.domain.repository.TagRepository
import com.vbshkn.ikollect.mockRoomTransactions
import com.vbshkn.ikollect.presentation.feature.photocards.profile.edit.EditPhotocardProfileUIState
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdatePhotocardUseCaseTest {
    private val photocardRepository = mockk<PhotocardRepository>(relaxed = true)
    private val imageRepository = mockk<ImageRepository>(relaxed = true)
    private val tagRepository = mockk<TagRepository>(relaxed = true)
    private val database = mockk<AppDatabase>(relaxed = true)
    private val updatePhotocardUseCase = UpdatePhotocardUseCase(photocardRepository,
        tagRepository,
        imageRepository,
        database
    )

    private fun getRealState(image: UserItemImage) = EditPhotocardProfileUIState(
        id = 1L,
        image = image,
        oldImageUrl = "old_image_uri",
        photocardName = "Photocard Name",
        userNotes = "Some notes",
        oldPhotocardName = "Old Photocard Name",
        oldTagIds = setOf(1L, 2L),
        selectedTagIds = setOf(2L, 3L)
    )

    private fun getStateWithEmptyFields(image: UserItemImage) = EditPhotocardProfileUIState(
        id = 1L,
        image = image,
        oldImageUrl = "old_image_uri",
        photocardName = "",
        userNotes = "",
        oldPhotocardName = "Old Photocard Name",
        oldTagIds = setOf(1L, 2L),
        selectedTagIds = setOf(2L, 3L)
    )

    private fun getRealEntity() = PhotocardEntity(
        photocardId = 1L,
        ownerId = 2L,
        albumId = 3L,
        displayName = "Old Photocard Name",
        userNote = "Old notes",
        imageUrl = "old_image_uri",
        isFavorite = true,
    )

    @BeforeEach
    fun setUp() {
        mockRoomTransactions(database)
        val realEntity = getRealEntity()
        coEvery { photocardRepository.getEntity(1L) } returns flowOf(realEntity)
        coEvery { photocardRepository.updatePhotocard(any()) } just Runs
        coEvery { tagRepository.updateLinks(any(), any(), any()) } just Runs
        coEvery { imageRepository.saveToInternalStorage(any()) } returns "saved_image_path"
        coEvery { imageRepository.deleteFromInternalStorage(any()) } just Runs
    }

    @Test
    fun `invoke should update photocard image if an internal one was chosen`() = runTest {
        // Given
        val mockedImage = mockk<UserItemImage>(relaxed = true) {
            every { isCached } returns false
            every { uri } returns "new_image_uri"
        }
        val mockedState = getRealState(mockedImage)

        // When
        updatePhotocardUseCase(mockedState)

        // Then
        coVerify(exactly = 0) { imageRepository.saveToInternalStorage("new_image_uri") }
        coVerify(exactly = 1) { imageRepository.deleteFromInternalStorage("old_image_uri") }
        coVerify(exactly = 1) {
            photocardRepository.updatePhotocard(match {
                it.photocardId == 1L &&
                        it.ownerId == 2L &&
                        it.albumId == 3L &&
                        it.displayName == "Photocard Name" &&
                        it.userNote == "Some notes" &&
                        it.imageUrl == "new_image_uri" &&
                        it.isFavorite
            })
        }
    }

    @Test
    fun `invoke should save image to the internal storage when it is cached`() = runTest {
        // Given
        val mockedImage = mockk<UserItemImage>(relaxed = true) {
            every { isCached } returns true
            every { uri } returns "cached_image_uri"
        }
        val mockedState = getRealState(mockedImage)

        // When
        updatePhotocardUseCase(mockedState)

        // Then
        coVerify(exactly = 1) { imageRepository.saveToInternalStorage("cached_image_uri") }
        coVerify(exactly = 1) { imageRepository.deleteFromInternalStorage("old_image_uri") }
        coVerify(exactly = 1) {
            photocardRepository.updatePhotocard(match {
                it.photocardId == 1L &&
                        it.ownerId == 2L &&
                        it.albumId == 3L &&
                        it.displayName == "Photocard Name" &&
                        it.userNote == "Some notes" &&
                        it.imageUrl == "saved_image_path" &&
                        it.isFavorite
            })
        }
    }

    @Test
    fun `invoke should not update photocard image if the same image was chosen`() = runTest {
        // Given
        val mockedImage = mockk<UserItemImage>(relaxed = true) {
            every { isCached } returns false
            every { uri } returns "old_image_uri"
        }
        val mockedState = getRealState(mockedImage)

        // When
        updatePhotocardUseCase(mockedState)

        // Then
        coVerify(exactly = 0) { imageRepository.saveToInternalStorage(any()) }
        coVerify(exactly = 0) { imageRepository.deleteFromInternalStorage(any()) }
        coVerify(exactly = 1) {
            photocardRepository.updatePhotocard(match {
                it.photocardId == 1L &&
                        it.ownerId == 2L &&
                        it.albumId == 3L &&
                        it.displayName == "Photocard Name" &&
                        it.userNote == "Some notes" &&
                        it.imageUrl == "old_image_uri" &&
                        it.isFavorite
            })
        }
    }

    @Test
    fun `invoke should not run when a photocard with given id doesn't exist`() = runTest {
        // Given
        val mockedImage = mockk<UserItemImage>(relaxed = true) {
            every { isCached } returns false
            every { uri } returns "old_image_uri"
        }
        val mockedState = getRealState(mockedImage).copy(id = 999L)
        coEvery { photocardRepository.getEntity(999L) } returns flowOf(null)

        // When
        updatePhotocardUseCase(mockedState)

        // Then
        coVerify(exactly = 0) { imageRepository.saveToInternalStorage(any()) }
        coVerify(exactly = 0) { imageRepository.deleteFromInternalStorage(any()) }
        coVerify(exactly = 0) { photocardRepository.updatePhotocard(any()) }
        coVerify(exactly = 0) { tagRepository.updateLinks(any(), any(), any()) }
    }

    @Test
    fun `invoke should update photocard with empty name and notes when the fields are blank`() = runTest {
        // Given
        val mockedImage = mockk<UserItemImage>(relaxed = true) {
            every { isCached } returns false
            every { uri } returns "old_image_uri"
        }
        val mockedState = getStateWithEmptyFields(mockedImage)

        // When
        updatePhotocardUseCase(mockedState)

        // Then
        coVerify(exactly = 1) {
            photocardRepository.updatePhotocard(match {
                it.photocardId == 1L &&
                        it.ownerId == 2L &&
                        it.albumId == 3L &&
                        it.displayName == "Old Photocard Name" &&
                        it.userNote == "" &&
                        it.imageUrl == "old_image_uri" &&
                        it.isFavorite
            })
        }
    }
}