package com.vbshkn.ikollect.domain.usecase.favorite

import com.vbshkn.ikollect.data.repository.ArtistRepositoryImpl
import com.vbshkn.ikollect.domain.repository.ArtistRepository
import javax.inject.Inject

class GetFavoriteArtistsUseCase @Inject constructor(
    private val artistsRepository: ArtistRepository
) {
    operator fun invoke() = artistsRepository.getFavorite()
}