package com.vbshkn.ikollect.domain.usecase

import com.vbshkn.ikollect.data.repository.ArtistRepository
import javax.inject.Inject

class GetArtistOverviewsUseCase @Inject constructor(
    private val artistRepository: ArtistRepository
) {
    operator fun invoke() = artistRepository.getArtistOverviews()
}