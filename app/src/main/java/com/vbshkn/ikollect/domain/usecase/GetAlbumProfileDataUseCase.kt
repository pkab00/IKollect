package com.vbshkn.ikollect.domain.usecase

import com.vbshkn.ikollect.data.repository.AlbumRepository
import javax.inject.Inject

class GetAlbumProfileDataUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    operator fun invoke(id: Long) = albumRepository.getAlbumProfile(id)
}