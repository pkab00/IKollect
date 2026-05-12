package com.vbshkn.ikollect.domain.usecase.favorite

import com.vbshkn.ikollect.data.repository.AlbumRepository
import javax.inject.Inject

class GetFavoriteAlbumsUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    operator fun invoke() = albumRepository.getFavoriteAlbums()
}