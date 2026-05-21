package com.vbshkn.ikollect.domain.usecase.save

import androidx.room.withTransaction
import com.vbshkn.ikollect.data.local.database.AppDatabase
import com.vbshkn.ikollect.data.local.model.entity.AlbumEntity
import com.vbshkn.ikollect.data.mapper.DataMappers.toEntity
import com.vbshkn.ikollect.data.repository.AlbumRepository
import com.vbshkn.ikollect.data.repository.ArtistRepository
import com.vbshkn.ikollect.data.repository.ImageRepository
import com.vbshkn.ikollect.di.ApplicationScope
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardUIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SaveAlbumUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    private val db: AppDatabase,
    @ApplicationScope private val applicationScope: CoroutineScope
){
    operator fun invoke(state: AlbumWizardUIState) = applicationScope.launch {
        val albumCandidate = state.albumCandidate!!
        val versionCandidate = state.versionCandidate!!
        val komcaNumber = state.komcaNumber

        val coverUri = if (state.coverImage?.isCached == true) {
            imageRepository.saveToInternalStorage(state.coverImage.uri)
        } else state.coverImage!!.uri

        db.withTransaction {
            albumCandidate.artistCandidates.forEach { artist ->
                artistRepository.insertToDatabase(artist.toEntity())
            }
            val albumEntity = AlbumEntity(
                masterId = albumCandidate.masterId,
                barcodeNumber = albumCandidate.barcodeNumber,
                komcaNumber = komcaNumber,
                name = albumCandidate.name,
                version = versionCandidate.name,
                releaseDate = albumCandidate.releaseDate,
                isFavorite = albumCandidate.isFavorite,
                imageUrl = coverUri,
                userNote = albumCandidate.userNote,
                isSynchronized = false
            )
            val artistIds = albumCandidate.artistCandidates.map { it.artistId }
            albumRepository.insertToDatabase(albumEntity, artistIds)

            albumCandidate.artistCandidates.forEach { candidate ->
                val memberIds = candidate.memberIds
                if (memberIds.isNotEmpty()) {
                    artistRepository.addGroupMembers(candidate.artistId, memberIds)
                }
            }
        }
    }
}