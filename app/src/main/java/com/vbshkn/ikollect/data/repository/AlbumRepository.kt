package com.vbshkn.ikollect.data.repository

import com.vbshkn.ikollect.data.AppError
import com.vbshkn.ikollect.data.DataMappers.toDomain
import com.vbshkn.ikollect.data.local.datasource.AlbumLocalDataSource
import com.vbshkn.ikollect.data.local.datasource.ArtistLocalDataSource
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.data.remote.dao.FullReleaseData
import com.vbshkn.ikollect.data.remote.datasource.AlbumRemoteDataSource
import com.vbshkn.ikollect.domain.model.Album
import com.vbshkn.ikollect.domain.model.AlbumCandidate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    fun getAlbumCandidate(barcode: String): Flow<NetworkResult<AlbumCandidate>> = flow {
        emit(NetworkResult.Loading)
        when(val webResponse = albumRemoteDS.getFullReleaseData(barcode)) {
            is NetworkResult.Success -> {
                if (validateStyles(webResponse.data)) {
                    emit(NetworkResult.Success(webResponse.data.toDomain()))
                }
                else {
                    emit(NetworkResult.Error(AppError.InvalidAlbumStyle))
                }
            }
            is NetworkResult.Error -> {
                emit(NetworkResult.Error(webResponse.error))
            }
            is NetworkResult.Loading -> {
                emit(NetworkResult.Loading)
            }
        }
    }

    private fun validateStyles(data: FullReleaseData): Boolean {
        val validStyles = listOf("k-pop", "k-rock", "j-pop")
        return data.releaseDetailsResponse.styles.any { style -> validStyles.contains(style.lowercase()) }
    }

    fun loadAllAlbums(): Flow<NetworkResult<List<Album>>> {
        return albumLocalDS.getAllWithArtists()
            .map { albumWithArtists ->
                val domainAlbums = albumWithArtists.map { it.toDomain() }
                NetworkResult.Success(domainAlbums) as NetworkResult<List<Album>>
            }
            .onStart { emit(NetworkResult.Loading) }
            .catch { emit(NetworkResult.Error(AppError.LocalDataLoadingError(it.localizedMessage ?: ""))) }
    }
}