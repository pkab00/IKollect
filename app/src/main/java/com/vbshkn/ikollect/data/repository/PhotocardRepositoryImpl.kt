package com.vbshkn.ikollect.data.repository

import com.vbshkn.ikollect.data.mapper.DataMappers.toListItem
import com.vbshkn.ikollect.data.local.datasource.PhotocardLocalDataSource
import com.vbshkn.ikollect.data.local.model.entity.PhotocardEntity
import com.vbshkn.ikollect.data.mapper.DataMappers.toProfile
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.list.PhotocardListItem
import com.vbshkn.ikollect.domain.model.profile.PhotocardProfileData
import com.vbshkn.ikollect.domain.repository.PhotocardRepository
import com.vbshkn.ikollect.util.asLocalResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PhotocardRepositoryImpl @Inject constructor(
    private val photocardLocalDS: PhotocardLocalDataSource
) : PhotocardRepository {
    override fun getAll(): Flow<NetworkResult<List<PhotocardListItem>>> {
        return photocardLocalDS.getAll()
            .asLocalResult { photocards ->
                photocards.map { it.toListItem() }
            }
    }

    override fun getFavorite(): Flow<NetworkResult<List<PhotocardListItem>>> {
        return photocardLocalDS.getFavorite()
            .asLocalResult { photocards ->
                photocards.map { it.toListItem() }
            }
    }

    override fun getByTag(tagId: Long): Flow<NetworkResult<List<PhotocardListItem>>> {
        return photocardLocalDS.getAllByTag(tagId)
            .asLocalResult { photocards ->
                photocards.map { it.toListItem() }
            }
    }

    override fun getPhotocardProfile(id: Long): Flow<NetworkResult<PhotocardProfileData?>> {
        return photocardLocalDS.getWithFullDetail(id).asLocalResult { it?.toProfile() }
    }

    override fun getEntity(id: Long): Flow<PhotocardEntity?> {
        return photocardLocalDS.getById(id)
    }

    override suspend fun updatePhotocard(updated: PhotocardEntity) {
        photocardLocalDS.update(updated)
    }

    override suspend fun insertWithArtists(
        entity: PhotocardEntity,
        artistIds: List<Long>
    ): Long {
        return photocardLocalDS.insertPhotocardWithArtists(entity, artistIds)
    }

    override suspend fun toggleFavorite(id: Long) {
        val current = photocardLocalDS.getById(id).first()?.isFavorite ?: return
        photocardLocalDS.setFavorite(id, !current)
    }

    override suspend fun softDelete(id: Long) {
        photocardLocalDS.setDeleted(id)
    }
}