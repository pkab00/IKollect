package com.vbshkn.ikollect.domain.usecase.get

import com.vbshkn.ikollect.data.repository.PhotocardRepositoryImpl
import com.vbshkn.ikollect.domain.repository.PhotocardRepository
import javax.inject.Inject

class GetAllPhotocardsUseCase @Inject constructor(
    private val photocardRepository: PhotocardRepository
) {
    operator fun invoke() = photocardRepository.getAll()
}