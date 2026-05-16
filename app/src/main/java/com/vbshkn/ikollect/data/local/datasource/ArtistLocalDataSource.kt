package com.vbshkn.ikollect.data.local.datasource

import com.vbshkn.ikollect.data.local.dao.ArtistDao
import com.vbshkn.ikollect.data.local.model.pojo.ArtistMinimalDetail
import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.model.pojo.ArtistFullDetail
import com.vbshkn.ikollect.data.local.model.pojo.GroupWithMembers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ArtistLocalDataSource @Inject constructor(
    private val dao: ArtistDao
) {
    fun getAll(): Flow<List<ArtistEntity>> {
        return dao.getAll()
    }

    fun getFavorite(): Flow<List<ArtistEntity>> {
        return dao.getFavorite()
    }


    fun getById(id: Long): Flow<ArtistEntity?> {
        return dao.getById(id)
    }

    fun getGroupWithMembers(groupId: Long): Flow<GroupWithMembers?> {
        return dao.getGroupWithMembers(groupId)
    }

    fun getArtistOverviews(): Flow<List<ArtistMinimalDetail>> {
        return dao.getArtistOverviews()
    }

    fun getWithFullDetail(id: Long): Flow<ArtistFullDetail?> {
        return dao.getWithFullDetail(id)
    }


    suspend fun insert(artistEntity: ArtistEntity) {
        dao.insert(artistEntity)
    }

    suspend fun insertAndLinkToGroup(
        member: ArtistEntity,
        groupId: Long
    ) {
        dao.insertAndLinkToGroup(member, groupId)
    }

    suspend fun setDeleted(id: Long) {
        dao.setDeleted(id)
    }

    suspend fun setFavorite(id: Long, isFavorite: Boolean) {
        dao.setFavorite(id, isFavorite)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}