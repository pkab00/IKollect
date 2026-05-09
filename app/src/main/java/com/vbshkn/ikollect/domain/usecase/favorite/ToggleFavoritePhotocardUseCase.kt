package com.vbshkn.ikollect.domain.usecase.favorite

import com.vbshkn.ikollect.data.repository.PhotocardRepository
import javax.inject.Inject

class ToggleFavoritePhotocardUseCase @Inject constructor(
    private val photocardRepository: PhotocardRepository
) {
    suspend operator fun invoke(id: Long, current: Boolean) {
        photocardRepository.toggleFavorite(id, current)
    }
}