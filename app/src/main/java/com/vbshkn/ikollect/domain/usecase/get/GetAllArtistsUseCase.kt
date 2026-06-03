package com.vbshkn.ikollect.domain.usecase.get

import com.vbshkn.ikollect.domain.repository.ArtistRepository
import javax.inject.Inject

class GetAllArtistsUseCase @Inject constructor(
    private val artistRepository: ArtistRepository
) {
    operator fun invoke() = artistRepository.getListItems()
}