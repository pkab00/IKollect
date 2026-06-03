package com.vbshkn.ikollect.domain.usecase.get

import com.vbshkn.ikollect.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class GetAllAlbumsByArtistUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    operator fun invoke(artistId: Long?) = if (artistId != null) {
        albumRepository.getListItemsByArtist(artistId)
    } else emptyFlow()
}