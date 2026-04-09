package com.vbshkn.ikollect.domain.usecase

import androidx.room.withTransaction
import com.vbshkn.ikollect.data.local.database.AppDatabase
import com.vbshkn.ikollect.data.local.model.entity.PhotocardEntity
import com.vbshkn.ikollect.data.repository.ImageRepository
import com.vbshkn.ikollect.data.repository.PhotocardRepository
import com.vbshkn.ikollect.data.repository.TagRepository
import com.vbshkn.ikollect.di.ApplicationScope
import com.vbshkn.ikollect.domain.model.candidate.PhotocardCandidate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SavePhotocardUseCase @Inject constructor(
    private val photocardRepository: PhotocardRepository,
    private val tagRepository: TagRepository,
    private val imageRepository: ImageRepository,
    private val db: AppDatabase,
    @ApplicationScope private val scope: CoroutineScope
) {
    operator fun invoke(candidate: PhotocardCandidate) = scope.launch {
        val savedImage = imageRepository.saveCacheToInternalStorage(candidate.imageUrl!!)
        val photocardEntity = PhotocardEntity(
            albumId = candidate.albumId,
            ownerId = candidate.ownerId!!,
            displayName = candidate.displayName,
            isFavorite = false,
            imageUrl = savedImage,
            userNote = candidate.userNote,
        )

        db.withTransaction {
            val id = photocardRepository.insertWithArtists(photocardEntity, candidate.depictedArtistsId)
            tagRepository.linkPhotocard(id, candidate.tagIds.toList())
        }
    }
}