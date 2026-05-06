package com.vbshkn.ikollect.domain.usecase

import androidx.room.withTransaction
import com.vbshkn.ikollect.data.local.database.AppDatabase
import com.vbshkn.ikollect.data.repository.AlbumRepository
import com.vbshkn.ikollect.data.repository.ImageRepository
import com.vbshkn.ikollect.util.now
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateAlbumUseCase @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val imageRepository: ImageRepository,
    private val db: AppDatabase
) {
    suspend operator fun invoke(
        id: Long,
        name: String,
        version: String,
        komcaNumber: String,
        userNotes: String,
        image: String?,
        oldImage: String?
    ) = db.withTransaction {
        var imagePath: String? = null
        if (image != oldImage) {
            imagePath = image?.let { imageRepository.saveToInternalStorage(it) }
            oldImage?.let { imageRepository.deleteFromInternalStorage(it) }
        }

        val updatedEntity = albumRepository
            .getEntity(id).first()?.copy(
                name = name,
                version = version,
                komcaNumber = komcaNumber.ifBlank { null },
                userNote = userNotes,
                imageUrl = imagePath ?: oldImage,
                isSynchronized = false,
                updatedAt = now()
            ) ?: return@withTransaction
        albumRepository.updateAlbum(updatedEntity)
    }
}