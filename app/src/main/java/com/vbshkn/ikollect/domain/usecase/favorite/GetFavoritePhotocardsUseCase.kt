package com.vbshkn.ikollect.domain.usecase.favorite

import com.vbshkn.ikollect.data.repository.PhotocardRepository
import javax.inject.Inject

class GetFavoritePhotocardsUseCase @Inject constructor(
    private val photocardRepository: PhotocardRepository
) {
    operator fun invoke() = photocardRepository.getFavorite()
}