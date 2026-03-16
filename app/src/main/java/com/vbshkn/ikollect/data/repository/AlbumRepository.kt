package com.vbshkn.ikollect.data.repository

import com.vbshkn.ikollect.data.DataMappers.toDomain
import com.vbshkn.ikollect.data.local.datasource.AlbumLocalDataSource
import com.vbshkn.ikollect.data.local.datasource.ArtistLocalDataSource
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.data.remote.datasource.AlbumRemoteDataSource
import com.vbshkn.ikollect.domain.model.Album
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    private val albumRemoteDS: AlbumRemoteDataSource,
    private val albumLocalDS: AlbumLocalDataSource,
    private val artistLocalDS: ArtistLocalDataSource
) {
    fun getAlbumCandidate(barcode: String) = flow {
        emit(NetworkResult.Loading)
        when(val webResponse = albumRemoteDS.getFullReleaseData(barcode)) {
            is NetworkResult.Success -> emit(webResponse.data.toDomain())
            else -> emit(webResponse)
        }

        fun loadAllAlbums(): Flow<NetworkResult<List<Album>>> {
            return albumLocalDS.getAllWithArtists()
                .map { albumWithArtists ->
                    val domainAlbums = albumWithArtists.map { it.toDomain() }
                    NetworkResult.Success(domainAlbums)
                }
                .onStart { emit(NetworkResult.Loading) }
                .catch { e -> emit(NetworkResult.Error(message = e.localizedMessage)) }
        }
    }
}