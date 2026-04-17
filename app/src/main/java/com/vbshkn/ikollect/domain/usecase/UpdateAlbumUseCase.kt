package com.vbshkn.ikollect.domain.usecase

import com.vbshkn.ikollect.data.repository.AlbumRepository
import com.vbshkn.ikollect.data.repository.ImageRepository
import javax.inject.Inject

class UpdateAlbumUseCase @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(
        id: Long,
        name: String,
        version: String,
        komcaNumber: String,
        userNotes: String,
        image: String?,
        oldImage: String?
    ) {
        var imagePath: String? = null
        if (image != oldImage) {
            imagePath = image?.let { imageRepository.saveToInternalStorage(it) }
            oldImage?.let { imageRepository.deleteFromInternalStorage(it) }
        }
        albumRepository.updateAlbum(
            id = id,
            name = name,
            version = version,
            komcaNumber = komcaNumber,
            userNotes = userNotes,
            imagePath = imagePath ?: oldImage
        )
    }
}