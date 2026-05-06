package com.vbshkn.ikollect.domain.usecase

import androidx.room.withTransaction
import com.vbshkn.ikollect.data.local.database.AppDatabase
import com.vbshkn.ikollect.data.repository.ImageRepository
import com.vbshkn.ikollect.data.repository.PhotocardRepository
import com.vbshkn.ikollect.data.repository.TagRepository
import com.vbshkn.ikollect.util.now
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdatePhotocardUseCase @Inject constructor(
    private val photocardRepository: PhotocardRepository,
    private val tagRepository: TagRepository,
    private val imageRepository: ImageRepository,
    private val db: AppDatabase
) {
    suspend operator fun invoke(
        id: Long,
        image: String?,
        oldImage: String?,
        photocardName: String,
        userNotes: String,
        oldTagIds: Set<Long>,
        selectedTagIds: Set<Long>
    ) = db.withTransaction {
        var imagePath: String? = null
        if (image != oldImage) {
            imagePath = image?.let { imageRepository.saveToInternalStorage(it) }
            oldImage?.let { imageRepository.deleteFromInternalStorage(it) }
        }

        val updatedEntity = photocardRepository
            .getEntity(id).first()?.copy(
                imageUrl = imagePath,
                displayName = photocardName,
                userNote = userNotes,
                isSynchronized = false,
                updatedAt = now()
            ) ?: return@withTransaction

        photocardRepository.updatePhotocard(updatedEntity)

        tagRepository.updateLinks(
            photocardId = id,
            oldTagIds = oldTagIds.toList(),
            newTagIds = selectedTagIds.toList()
        )
    }
}