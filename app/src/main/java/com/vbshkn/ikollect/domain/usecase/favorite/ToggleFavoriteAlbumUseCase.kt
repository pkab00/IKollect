package com.vbshkn.ikollect.domain.usecase.favorite

import com.vbshkn.ikollect.data.repository.AlbumRepositoryImpl
import com.vbshkn.ikollect.domain.repository.AlbumRepository
import javax.inject.Inject

class ToggleFavoriteAlbumUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    suspend operator fun invoke(id: Long, current: Boolean) {
        albumRepository.toggleFavorite(id, current)
    }
}