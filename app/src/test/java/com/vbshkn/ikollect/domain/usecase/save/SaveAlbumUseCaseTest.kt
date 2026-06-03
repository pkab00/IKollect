package com.vbshkn.ikollect.domain.usecase.save

import androidx.room.withTransaction
import com.vbshkn.ikollect.data.local.database.AppDatabase
import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity
import com.vbshkn.ikollect.domain.model.UserItemImage
import com.vbshkn.ikollect.domain.model.candidate.AlbumCandidate
import com.vbshkn.ikollect.domain.model.candidate.ArtistCandidate
import com.vbshkn.ikollect.domain.model.candidate.VersionCandidate
import com.vbshkn.ikollect.domain.repository.AlbumRepository
import com.vbshkn.ikollect.domain.repository.ArtistRepository
import com.vbshkn.ikollect.domain.repository.ImageRepository
import com.vbshkn.ikollect.domain.usecase.mockRoomTransactions
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardUIState
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SaveAlbumUseCaseTest {
    private val imageRepository = mockk<ImageRepository>()
    private val artistRepository = mockk<ArtistRepository>()
    private val albumRepository = mockk<AlbumRepository>()
    private val database = mockk<AppDatabase>()
    private val scope = TestScope()
    private val saveAlbumUseCase = SaveAlbumUseCase(
        imageRepository,
        artistRepository,
        albumRepository,
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
        val mockArtistCandidate = mockk<ArtistCandidate>(relaxed = true) {
            every { artistId } returns 123L
            every { memberIds } returns listOf(1L, 2L, 3L)
        }
        val mockAlbumCandidate = mockk<AlbumCandidate>(relaxed = true) {
            every { artistCandidates } returns listOf(mockArtistCandidate)
        }
        val mockVersionCandidate = mockk<VersionCandidate>(relaxed = true)
        val mockImage = mockk<UserItemImage>(relaxed = true) {
            every { isCached } returns true
            every { uri } returns "content://cached_cover"
        }
        val state = AlbumWizardUIState(
            albumCandidate = mockAlbumCandidate,
            versionCandidate = mockVersionCandidate,
            coverImage = mockImage,
            komcaNumber = "SAMPLE_KOMCA"
        )

        coEvery { imageRepository.saveToInternalStorage(any()) } returns "internal://saved_cover"
        coEvery { albumRepository.insertToDatabase(any(), any()) } returns 1L
        coEvery { artistRepository.insertToDatabase(any()) } just Runs
        coEvery { artistRepository.addGroupMembers(any(), any()) } just Runs

        // When
        saveAlbumUseCase(state)
        scope.advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { imageRepository.saveToInternalStorage("content://cached_cover") }
        coVerify(exactly = 1) {
            artistRepository.insertToDatabase(
                entity = match { it.artistId == 123L }
            )
        }
        coVerify(exactly = 1) {
            albumRepository.insertToDatabase(
                album = match { it.imageUrl == "internal://saved_cover" && it.komcaNumber == "SAMPLE_KOMCA" },
                artistIds = listOf(123L)
            )
        }
        coVerify(exactly = 1) { artistRepository.addGroupMembers(123L, listOf(1L, 2L, 3L)) }
    }

    @Test
    fun `invoke should save albums with non-cached covers as they are`() = runTest {
        // Given
        val mockArtistCandidate = mockk<ArtistCandidate>(relaxed = true) {
            every { artistId } returns 123L
            every { memberIds } returns listOf(1L, 2L, 3L)
        }
        val mockAlbumCandidate = mockk<AlbumCandidate>(relaxed = true) {
            every { artistCandidates } returns listOf(mockArtistCandidate)
        }
        val mockVersionCandidate = mockk<VersionCandidate>(relaxed = true)
        val mockImage = mockk<UserItemImage>(relaxed = true) {
            every { isCached } returns false
            every { uri } returns "internal://original_saved_cover"
        }
        val state = AlbumWizardUIState(
            albumCandidate = mockAlbumCandidate,
            versionCandidate = mockVersionCandidate,
            coverImage = mockImage,
            komcaNumber = "SAMPLE_KOMCA"
        )

        coEvery { imageRepository.saveToInternalStorage(any()) } returns "internal://saved_cover_from_repo"
        coEvery { albumRepository.insertToDatabase(any(), any()) } returns 1L
        coEvery { artistRepository.insertToDatabase(any()) } just Runs
        coEvery { artistRepository.addGroupMembers(any(), any()) } just Runs

        // When
        saveAlbumUseCase(state)
        scope.advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { imageRepository.saveToInternalStorage(any()) }
        coVerify(exactly = 1) {
            artistRepository.insertToDatabase(
                entity = match { it.artistId == 123L }
            )
        }
        coVerify(exactly = 1) {
            albumRepository.insertToDatabase(
                album = match { it.imageUrl == "internal://original_saved_cover" && it.komcaNumber == "SAMPLE_KOMCA" },
                artistIds = listOf(123L)
            )
        }
        coVerify(exactly = 1) { artistRepository.addGroupMembers(123L, listOf(1L, 2L, 3L)) }
    }
}