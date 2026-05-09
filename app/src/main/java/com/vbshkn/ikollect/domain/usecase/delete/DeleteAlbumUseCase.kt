package com.vbshkn.ikollect.domain.usecase.delete

import com.vbshkn.ikollect.data.repository.AlbumRepository
import javax.inject.Inject

class DeleteAlbumUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    suspend operator fun invoke(id: Long) {
        albumRepository.softDelete(id)
    }
}