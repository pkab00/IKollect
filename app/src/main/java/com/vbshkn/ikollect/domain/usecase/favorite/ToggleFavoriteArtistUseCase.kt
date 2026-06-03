package com.vbshkn.ikollect.domain.usecase.favorite

import com.vbshkn.ikollect.data.repository.ArtistRepositoryImpl
import com.vbshkn.ikollect.domain.repository.ArtistRepository
import javax.inject.Inject

class ToggleFavoriteArtistUseCase @Inject constructor(
    private val artistRepository: ArtistRepository
) {
    suspend operator fun invoke(id: Long) {
        artistRepository.toggleFavorite(id)
    }
}