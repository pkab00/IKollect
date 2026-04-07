package com.vbshkn.ikollect.domain.usecase

import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.data.repository.AlbumRepository
import com.vbshkn.ikollect.domain.model.details.AlbumDetails
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllAlbumsUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    operator fun invoke(): Flow<NetworkResult<List<AlbumDetails>>> {
        return albumRepository.getAllAlbums()
    }
}