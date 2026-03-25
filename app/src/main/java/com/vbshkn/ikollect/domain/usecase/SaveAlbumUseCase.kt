package com.vbshkn.ikollect.domain.usecase

import androidx.room.withTransaction
import com.vbshkn.ikollect.data.DataMappers.toEntity
import com.vbshkn.ikollect.data.local.database.AppDatabase
import com.vbshkn.ikollect.data.local.entity.AlbumEntity
import com.vbshkn.ikollect.data.repository.AlbumRepository
import com.vbshkn.ikollect.data.repository.ArtistRepository
import com.vbshkn.ikollect.data.repository.ImageRepository
import com.vbshkn.ikollect.presentation.feature.addalbum.AddAlbumUIState
import javax.inject.Inject

class SaveAlbumUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    private val db: AppDatabase
){
    suspend operator fun invoke(state: AddAlbumUIState) {
        val albumCandidate = state.albumCandidate
        val versionCandidate = state.versionCandidate!!
        val komcaNumber = state.komcaNumber

        val coverUri = if (state.isCoverCached) {
            imageRepository.saveCacheToInternalStorage(versionCandidate.coverImage!!)
        } else versionCandidate.coverImage!!

        db.withTransaction {
            albumCandidate.artists.forEach { artist ->
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
                userNote = albumCandidate.userNote
            )
            val artistIds = albumCandidate.artists.map { it.artistId }
            albumRepository.insertToDatabase(albumEntity, artistIds)
        }
    }
}