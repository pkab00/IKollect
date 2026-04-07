package com.vbshkn.ikollect.data.repository

import com.vbshkn.ikollect.data.mapper.DataMappers.toListItem
import com.vbshkn.ikollect.data.local.datasource.PhotocardLocalDataSource
import com.vbshkn.ikollect.data.local.model.entity.PhotocardEntity
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.list.PhotocardListItem
import com.vbshkn.ikollect.util.asLocalResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhotocardRepository @Inject constructor(
    private val photocardLocalDS: PhotocardLocalDataSource
) {
    fun getAll(): Flow<NetworkResult<List<PhotocardListItem>>> {
        return photocardLocalDS.getAllWithArtists()
            .asLocalResult { photocards ->
                photocards.map { it.toListItem() }
            }
    }

    suspend fun insertWithArtists(
        entity: PhotocardEntity,
        artistIds: List<Long>
    ) : Long {
        return photocardLocalDS.insertPhotocardWithArtists(entity, artistIds)
    }
}