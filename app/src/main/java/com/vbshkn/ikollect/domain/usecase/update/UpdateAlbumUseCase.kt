package com.vbshkn.ikollect.domain.usecase.update

import androidx.room.withTransaction
import com.vbshkn.ikollect.data.local.database.AppDatabase
import com.vbshkn.ikollect.data.repository.AlbumRepositoryImpl
import com.vbshkn.ikollect.data.repository.ImageRepositoryImpl
import com.vbshkn.ikollect.domain.model.UserItemImage
import com.vbshkn.ikollect.domain.repository.AlbumRepository
import com.vbshkn.ikollect.domain.repository.ImageRepository
import com.vbshkn.ikollect.presentation.feature.albums.profile.edit.EditAlbumProfileDialogState
import com.vbshkn.ikollect.presentation.feature.albums.profile.edit.EditAlbumProfileUIState
import com.vbshkn.ikollect.util.now
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateAlbumUseCase @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val imageRepository: ImageRepository,
    private val db: AppDatabase
) {
    suspend operator fun invoke(state: EditAlbumProfileUIState) = db.withTransaction {
        val entityToUpdate = state.id?.let { albumRepository.getEntity(it).first() }
            ?: return@withTransaction

        var imagePath: String? = null
        if (state.image?.uri != state.oldImageUrl) {
            imagePath = state.image?.let {
                if (it.isCached) imageRepository.saveToInternalStorage(it.uri)
                else it.uri
            }
            state.oldImageUrl?.let {
                imageRepository.deleteFromInternalStorage(it)
            }
        }

        val updatedEntity = entityToUpdate.copy(
                name = state.albumName.ifBlank { state.oldAlbumName },
                version = state.albumVersion.ifBlank { state.oldAlbumVersion },
                komcaNumber = state.komcaNumber.ifBlank { null },
                userNote = state.userNotes,
                imageUrl = imagePath ?: state.oldImageUrl,
                isSynchronized = false,
                updatedAt = now()
            )
        albumRepository.updateAlbum(updatedEntity)
    }
}