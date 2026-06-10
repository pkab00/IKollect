package com.vbshkn.ikollect.domain.usecase.get

import com.vbshkn.ikollect.domain.repository.PhotocardRepository
import javax.inject.Inject

class GetPhotocardsByTagUseCase @Inject constructor(
    private val photocardRepository: PhotocardRepository
) {
    operator fun invoke(tagId: Long) = photocardRepository.getByTag(tagId)
}