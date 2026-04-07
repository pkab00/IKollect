package com.vbshkn.ikollect.domain.usecase

import com.vbshkn.ikollect.data.repository.AlbumRepository
import javax.inject.Inject

class GetArtistAlbumListUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    operator fun invoke(artistId: Long) = albumRepository.getAllByArtist(artistId)
}