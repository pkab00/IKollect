package com.vbshkn.ikollect.domain.usecase.get

import com.vbshkn.ikollect.data.repository.ArtistRepository
import javax.inject.Inject

class GetArtistListUseCase @Inject constructor(
    private val artistRepository: ArtistRepository
) {
    operator fun invoke() = artistRepository.getListItems()
}