package com.vbshkn.ikollect.domain.usecase.update

import android.util.Log
import androidx.room.withTransaction
import com.vbshkn.ikollect.data.local.database.AppDatabase
import com.vbshkn.ikollect.data.repository.ImageRepositoryImpl
import com.vbshkn.ikollect.data.repository.PhotocardRepositoryImpl
import com.vbshkn.ikollect.data.repository.TagRepositoryImpl
import com.vbshkn.ikollect.domain.model.UserItemImage
import com.vbshkn.ikollect.domain.repository.ImageRepository
import com.vbshkn.ikollect.domain.repository.PhotocardRepository
import com.vbshkn.ikollect.domain.repository.TagRepository
import com.vbshkn.ikollect.presentation.feature.photocards.profile.edit.EditPhotocardProfileUIState
import com.vbshkn.ikollect.util.now
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdatePhotocardUseCase @Inject constructor(
    private val photocardRepository: PhotocardRepository,
    private val tagRepository: TagRepository,
    private val imageRepository: ImageRepository,
    private val db: AppDatabase
) {
    suspend operator fun invoke(state: EditPhotocardProfileUIState) = db.withTransaction {
        val entityToModify = state.id?.let {
            photocardRepository.getEntity(it).first()
        } ?: return@withTransaction

        var imagePath: String?
        if (state.image?.uri != state.oldImageUrl) {
            imagePath = state.image?.let {
                if (it.isCached) imageRepository.saveToInternalStorage(it.uri)
                else it.uri
            }
            state.oldImageUrl?.let {
                imageRepository.deleteFromInternalStorage(it)
            }
        }
        else {
            imagePath = state.oldImageUrl
        }

        val updatedEntity = entityToModify.copy(
                imageUrl = imagePath,
                displayName = state.photocardName.ifBlank { state.oldPhotocardName },
                userNote = state.userNotes,
                isSynchronized = false,
                updatedAt = now()
            )

        photocardRepository.updatePhotocard(updatedEntity)

        Log.d("SSS", state.oldTagIds.joinToString())
        Log.d("SSS", state.selectedTagIds.joinToString())

        tagRepository.updateLinks(
            photocardId = state.id,
            oldTagIds = state.oldTagIds.toList(),
            newTagIds = state.selectedTagIds.toList()
        )
    }
}