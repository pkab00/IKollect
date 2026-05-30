package com.vbshkn.ikollect.domain.usecase.favorite

import com.vbshkn.ikollect.data.repository.AlbumRepositoryImpl
import com.vbshkn.ikollect.domain.repository.AlbumRepository
import javax.inject.Inject

class GetFavoriteAlbumsUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    operator fun invoke() = albumRepository.getFavoriteAlbums()
}