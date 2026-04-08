package com.vbshkn.ikollect.domain.usecase

import com.vbshkn.ikollect.data.repository.PhotocardRepository
import javax.inject.Inject

class GetAllPhotocardsUseCase @Inject constructor(
    private val photocardRepository: PhotocardRepository
) {
    operator fun invoke() = photocardRepository.getAll()
}