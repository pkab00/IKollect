package com.vbshkn.ikollect.domain.usecase.get

import com.vbshkn.ikollect.data.repository.PhotocardRepositoryImpl
import com.vbshkn.ikollect.domain.repository.PhotocardRepository
import javax.inject.Inject

class GetPhotocardProfileDataUseCase @Inject constructor(
    private val photocardRepository: PhotocardRepository
) {
    operator fun invoke(id: Long) = photocardRepository.getPhotocardProfile(id)
}