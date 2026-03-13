package com.vbshkn.ikollect.data.local.datasource

import com.vbshkn.ikollect.data.local.dao.ArtistDao
import com.vbshkn.ikollect.data.local.entity.ArtistEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ArtistLocalDataSource @Inject constructor(
    private val dao: ArtistDao
) {
    fun getAll(): Flow<List<ArtistEntity>> {
        return dao.getAll()
    }

    suspend fun insert(artistEntity: ArtistEntity) {
        dao.insert(artistEntity)
    }
}