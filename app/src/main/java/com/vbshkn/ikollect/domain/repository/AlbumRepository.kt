package com.vbshkn.ikollect.domain.repository

import com.vbshkn.ikollect.data.local.model.entity.AlbumEntity
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.candidate.AlbumCandidate
import com.vbshkn.ikollect.domain.model.details.AlbumDetails
import com.vbshkn.ikollect.domain.model.list.AlbumListItem
import com.vbshkn.ikollect.domain.model.profile.AlbumProfileData
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    fun getAlbumCandidate(barcode: String): Flow<NetworkResult<AlbumCandidate>>
    fun getEntity(id: Long): Flow<AlbumEntity?>
    fun getAllDetails(): Flow<NetworkResult<List<AlbumDetails>>>
    fun getListItems(): Flow<NetworkResult<List<AlbumListItem>>>
    fun getFavoriteAlbums(): Flow<NetworkResult<List<AlbumDetails>>>
    fun getListItemsByArtist(artistId: Long): Flow<NetworkResult<List<AlbumListItem>>>
    fun getAlbumProfile(id: Long): Flow<NetworkResult<AlbumProfileData?>>

    suspend fun insertToDatabase(
        album: AlbumEntity,
        artistIds: List<Long>
    ): Long

    suspend fun updateAlbum(updated: AlbumEntity)

    suspend fun toggleFavorite(id: Long)

    suspend fun softDelete(id: Long)
}