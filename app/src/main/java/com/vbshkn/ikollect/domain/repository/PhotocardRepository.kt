package com.vbshkn.ikollect.domain.repository

import com.vbshkn.ikollect.data.local.model.entity.PhotocardEntity
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.list.PhotocardListItem
import com.vbshkn.ikollect.domain.model.profile.PhotocardProfileData
import kotlinx.coroutines.flow.Flow

interface PhotocardRepository {
    fun getAll(): Flow<NetworkResult<List<PhotocardListItem>>>
    fun getFavorite(): Flow<NetworkResult<List<PhotocardListItem>>>
    fun getByTag(tagId: Long): Flow<NetworkResult<List<PhotocardListItem>>>
    fun getPhotocardProfile(id: Long): Flow<NetworkResult<PhotocardProfileData?>>
    fun getEntity(id: Long): Flow<PhotocardEntity?>

    suspend fun updatePhotocard(updated: PhotocardEntity)

    suspend fun insertWithArtists(
        entity: PhotocardEntity,
        artistIds: List<Long>
    ): Long

    suspend fun toggleFavorite(id: Long)

    suspend fun softDelete(id: Long)
}