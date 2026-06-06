package com.vbshkn.ikollect.domain.usecase.update

import com.vbshkn.ikollect.data.local.database.AppDatabase
import com.vbshkn.ikollect.data.local.model.entity.AlbumEntity
import com.vbshkn.ikollect.domain.model.UserItemImage
import com.vbshkn.ikollect.domain.repository.AlbumRepository
import com.vbshkn.ikollect.domain.repository.ImageRepository
import com.vbshkn.ikollect.mockRoomTransactions
import com.vbshkn.ikollect.presentation.feature.albums.profile.edit.EditAlbumProfileUIState
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

class UpdateAlbumUseCaseTest {
    private val albumRepository = mockk<AlbumRepository>(relaxed = true)
    private val imageRepository = mockk<ImageRepository>(relaxed = true)
    private val database = mockk<AppDatabase>(relaxed = true)
    private val updateAlbumUseCase = UpdateAlbumUseCase(albumRepository, imageRepository, database)

    private fun getRealState(imageItem: UserItemImage) = EditAlbumProfileUIState(
        id = 1L,
        image = imageItem,
        oldImageUrl = "old_image_uri",
        albumName = "Album Name",
        albumVersion = "Album Version",
        komcaNumber = "12345",
        userNotes = "Some notes"
    )

    private fun getStateWithEmptyFields(imageItem: UserItemImage) = EditAlbumProfileUIState(
        id = 1L,
        image = imageItem,
        oldImageUrl = "old_image_uri",
        oldAlbumName = "Album Name",
        oldAlbumVersion = "Album Version",
        albumName = "",
        albumVersion = "",
        komcaNumber = "",
        userNotes = ""
    )

    private fun getRealEntity() = AlbumEntity(
        albumId = 1L,
        name = "Old Album Name",
        version = "Old Album Version",
        komcaNumber = "54321",
        userNote = "Old notes",
        imageUrl = "old_image_uri",
        masterId = 2L,
        barcodeNumber = "393823283",
        releaseDate = "2012",
        isFavorite = true,
    )

    @BeforeEach
    fun setUp() {
        mockRoomTransactions(database)
        val mockedEntity = getRealEntity()
        coEvery { albumRepository.getEntity(1L) } returns flowOf(mockedEntity)
        coEvery { albumRepository.updateAlbum(any()) } just Runs
        coEvery { imageRepository.saveToInternalStorage(any()) } returns "saved_image_path"
        coEvery { imageRepository.deleteFromInternalStorage(any()) } just Runs
    }

    @Test
    fun `invoke should update album cover if new internal image was chosen`() = runTest {
        // Given
        val mockedImage = mockk<UserItemImage>(relaxed = true) {
            every { isCached } returns false
            every { uri } returns "new_image_uri"
        }
        val mockedState = getRealState(mockedImage)

        // When
        updateAlbumUseCase(mockedState)

        // Then
        coVerify(exactly = 0) { imageRepository.saveToInternalStorage(any()) }
        coVerify(exactly = 1) { imageRepository.deleteFromInternalStorage("old_image_uri") }
        coVerify(exactly = 1) { albumRepository.updateAlbum(match {
            it.albumId == 1L &&
            it.name == "Album Name" &&
            it.version == "Album Version" &&
            it.komcaNumber == "12345" &&
            it.userNote == "Some notes" &&
            it.imageUrl == "new_image_uri"
        }) }
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
        updateAlbumUseCase(mockedState)

        // Then
        coVerify(exactly = 1) { imageRepository.saveToInternalStorage("cached_image_uri") }
        coVerify(exactly = 1) { imageRepository.deleteFromInternalStorage("old_image_uri") }
        coVerify(exactly = 1) { albumRepository.updateAlbum(match {
            it.albumId == 1L &&
                    it.name == "Album Name" &&
                    it.version == "Album Version" &&
                    it.komcaNumber == "12345" &&
                    it.userNote == "Some notes" &&
                    it.imageUrl == "saved_image_path"
        }) }
    }

    @Test
    fun `invoke should not update album cover if the same image was chosen`() = runTest {
        // Given
        val mockedImage = mockk<UserItemImage>(relaxed = true) {
            every { isCached } returns false
            every { uri } returns "old_image_uri"
        }
        val mockedState = getRealState(mockedImage)

        // When
        updateAlbumUseCase(mockedState)

        // Then
        coVerify(exactly = 0) { imageRepository.saveToInternalStorage(any()) }
        coVerify(exactly = 0) { imageRepository.deleteFromInternalStorage(any()) }
        coVerify(exactly = 1) { albumRepository.updateAlbum(match {
            it.albumId == 1L &&
                    it.name == "Album Name" &&
                    it.version == "Album Version" &&
                    it.komcaNumber == "12345" &&
                    it.userNote == "Some notes" &&
                    it.imageUrl == "old_image_uri"
        }) }
    }

    @Test
    fun `invoke should not update album if entity with given id does not exist`() = runTest {
        // Given
        coEvery { albumRepository.getEntity(1L) } returns flowOf(null)
        val mockedState = getRealState(mockk(relaxed = true))

        // When
        updateAlbumUseCase(mockedState)

        // Then
        coVerify(exactly = 0) { imageRepository.saveToInternalStorage(any()) }
        coVerify(exactly = 0) { imageRepository.deleteFromInternalStorage(any()) }
        coVerify(exactly = 0) { albumRepository.updateAlbum(any()) }
    }

    @Test
    fun `invoke should not update some fields when they are left empty`() = runTest {
        // Given
        val mockedImage = mockk<UserItemImage>(relaxed = true) {
            every { isCached } returns false
            every { uri } returns "old_image_uri"
        }
        val mockedState = getStateWithEmptyFields(mockedImage)

        // When
        updateAlbumUseCase(mockedState)

        // Then
        coVerify(exactly = 0) { imageRepository.saveToInternalStorage(any()) }
        coVerify(exactly = 0) { imageRepository.deleteFromInternalStorage(any()) }
        coVerify(exactly = 1) { albumRepository.updateAlbum(match {
            it.albumId == 1L &&
                    it.name == "Album Name" &&
                    it.version == "Album Version" &&
                    it.komcaNumber == null &&
                    it.userNote == "" &&
                    it.imageUrl == "old_image_uri"
        }) }
    }
}