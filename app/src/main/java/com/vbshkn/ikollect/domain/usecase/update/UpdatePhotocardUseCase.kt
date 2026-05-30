package com.vbshkn.ikollect.domain.usecase.update

import androidx.room.withTransaction
import com.vbshkn.ikollect.data.local.database.AppDatabase
import com.vbshkn.ikollect.data.repository.ImageRepositoryImpl
import com.vbshkn.ikollect.data.repository.PhotocardRepositoryImpl
import com.vbshkn.ikollect.data.repository.TagRepositoryImpl
import com.vbshkn.ikollect.domain.model.UserItemImage
import com.vbshkn.ikollect.domain.repository.ImageRepository
import com.vbshkn.ikollect.domain.repository.PhotocardRepository
import com.vbshkn.ikollect.domain.repository.TagRepository
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
        image: UserItemImage?,
        oldImage: String?,
        photocardName: String,
        userNotes: String,
        oldTagIds: Set<Long>,
        selectedTagIds: Set<Long>
    ) = db.withTransaction {
        var imagePath: String?
        if (image?.uri != oldImage) {
            imagePath = image?.let {
                if (it.isCached) imageRepository.saveToInternalStorage(it.uri)
                else it.uri
            }
            oldImage?.let {
                imageRepository.deleteFromInternalStorage(it)
            }
        }
        else {
            imagePath = oldImage
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