package com.vbshkn.ikollect.data.local.datasource

import com.vbshkn.ikollect.data.local.dao.PhotocardDao
import com.vbshkn.ikollect.data.local.model.entity.PhotocardEntity
import com.vbshkn.ikollect.data.local.model.pojo.ArtistWithPhotocards
import com.vbshkn.ikollect.data.local.model.pojo.PhotocardFullDetail
import com.vbshkn.ikollect.data.local.model.pojo.PhotocardMinimalDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhotocardLocalDataSource @Inject constructor(
    private val dao: PhotocardDao
) {
    fun getAll(): Flow<List<PhotocardEntity>> {
        return dao.getAll()
    }

    fun getAllWithArtists(): Flow<List<PhotocardMinimalDetail>> {
        return dao.getAllWithArtists()
    }

    fun getAllByArtist(artistId: Long): Flow<ArtistWithPhotocards?> {
        return dao.getAllByArtist(artistId)
    }

    fun getWithFullDetail(id: Long): Flow<PhotocardFullDetail?> {
        return dao.getWithFullDetail(id)
    }

    suspend fun insertPhotocardWithArtists(
        photocardEntity: PhotocardEntity,
        artistIds: List<Long>
    ) : Long {
        return dao.insertPhotocardWithArtists(photocardEntity, artistIds)
    }
}