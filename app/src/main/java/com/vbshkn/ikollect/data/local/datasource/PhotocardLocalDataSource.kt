package com.vbshkn.ikollect.data.local.datasource

import com.vbshkn.ikollect.data.local.dao.PhotocardDao
import com.vbshkn.ikollect.data.local.entity.PhotocardEntity
import com.vbshkn.ikollect.data.local.pojo.ArtistWithPhotocards
import com.vbshkn.ikollect.data.local.pojo.PhotocardWithArtists
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhotocardLocalDataSource @Inject constructor(
    private val dao: PhotocardDao
) {
    fun getAll(): Flow<List<PhotocardEntity>> {
        return dao.getAll()
    }

    fun getAllWithArtists(): Flow<List<PhotocardWithArtists>> {
        return dao.getAllWithArtists()
    }

    fun getAllByArtist(artistId: Long): Flow<ArtistWithPhotocards?> {
        return dao.getAllByArtist(artistId)
    }

    suspend fun insertPhotocardWithArtists(
        photocardEntity: PhotocardEntity,
        artistIds: List<Long>
    ) {
        dao.insertPhotocardWithArtists(photocardEntity, artistIds)
    }
}