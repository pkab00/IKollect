package com.vbshkn.ikollect.data.local.datasource

import com.vbshkn.ikollect.data.local.dao.ArtistDao
import com.vbshkn.ikollect.data.local.model.ArtistOverview
import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.model.pojo.GroupWithMembers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ArtistLocalDataSource @Inject constructor(
    private val dao: ArtistDao
) {
    fun getAll(): Flow<List<ArtistEntity>> {
        return dao.getAll()
    }

    fun getById(id: Long): Flow<ArtistEntity?> {
        return dao.getById(id)
    }

    fun getGroupWithMembers(groupId: Long): Flow<GroupWithMembers?> {
        return dao.getGroupWithMembers(groupId)
    }

    fun getArtistOverviews(): Flow<List<ArtistOverview>> {
        return dao.getArtistOverviews()
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
}