package com.vbshkn.ikollect.data.repository

import com.vbshkn.ikollect.domain.error.AppError
import com.vbshkn.ikollect.data.mapper.DataMappers.toListItem
import com.vbshkn.ikollect.data.mapper.DataMappers.toDetails
import com.vbshkn.ikollect.data.mapper.DataMappers.toCandidate
import com.vbshkn.ikollect.data.local.datasource.AlbumLocalDataSource
import com.vbshkn.ikollect.data.local.model.entity.AlbumEntity
import com.vbshkn.ikollect.data.mapper.DataMappers.toEntity
import com.vbshkn.ikollect.data.mapper.DataMappers.toProfile
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.data.remote.dao.FullReleaseData
import com.vbshkn.ikollect.data.remote.datasource.AlbumRemoteDataSource
import com.vbshkn.ikollect.domain.model.details.AlbumDetails
import com.vbshkn.ikollect.domain.model.candidate.AlbumCandidate
import com.vbshkn.ikollect.domain.model.list.AlbumListItem
import com.vbshkn.ikollect.domain.model.profile.AlbumProfileData
import com.vbshkn.ikollect.util.asLocalResult
import com.vbshkn.ikollect.util.now
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    private val albumRemoteDS: AlbumRemoteDataSource,
    private val albumLocalDS: AlbumLocalDataSource
) {
    fun getAlbumCandidate(barcode: String): Flow<NetworkResult<AlbumCandidate>> = flow {
        emit(NetworkResult.Loading)
        when(val webResponse = albumRemoteDS.getFullReleaseData(barcode)) {
            is NetworkResult.Success -> {
                if (validateStyles(webResponse.data)) {
                    emit(NetworkResult.Success(webResponse.data.toCandidate()))
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

    fun getEntity(id: Long): Flow<AlbumEntity?> {
        return albumLocalDS.getById(id)
    }

    fun getAllAlbums(): Flow<NetworkResult<List<AlbumDetails>>> {
        return albumLocalDS.getAllWithArtists()
            .asLocalResult { albums ->
                albums.map { it.toDetails() }
            }
    }

    fun getFavoriteAlbums(): Flow<NetworkResult<List<AlbumDetails>>> {
        return albumLocalDS.getFavoriteWithArtists()
            .asLocalResult { albums ->
                albums.map { it.toDetails() }
            }
    }

    fun getAllByArtist(artistId: Long): Flow<NetworkResult<List<AlbumListItem>>> {
        return albumLocalDS.getAllByArtist(artistId)
            .asLocalResult { artistsWithAlbums ->
                artistsWithAlbums
                    .flatMap { it.albums }
                    .map { it.toListItem() }
            }
    }

    fun getAlbumProfile(id: Long): Flow<NetworkResult<AlbumProfileData?>> {
        return albumLocalDS.getWithFullDetail(id).asLocalResult { it?.toProfile() }
    }

    suspend fun insertToDatabase(
        album: AlbumEntity,
        artistIds: List<Long>
    ) {
        albumLocalDS.insertAlbumWithArtists(album, artistIds)
    }

    suspend fun updateAlbum(updated: AlbumEntity) {
        albumLocalDS.updateAlbum(updated)
    }

    suspend fun toggleFavorite(id: Long, oldValue: Boolean) {
        albumLocalDS.setFavorite(id, !oldValue)
    }

    suspend fun softDelete(id: Long) {
        albumLocalDS.setDeleted(id)
    }
}