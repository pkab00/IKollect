package com.vbshkn.ikollect.domain.usecase

import com.vbshkn.ikollect.data.repository.AlbumRepository
import com.vbshkn.ikollect.domain.model.AlbumCandidate
import javax.inject.Inject

class UpdateActiveCandidateUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    operator fun invoke(albumCandidate: AlbumCandidate?) {
        if (albumCandidate == null) {
            albumRepository.clearCandidate()
        }
        else {
            albumRepository.setCandidate(albumCandidate)
        }
    }
}