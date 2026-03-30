package com.vbshkn.ikollect.data.repository

import com.vbshkn.ikollect.data.DataMappers.toDomain
import com.vbshkn.ikollect.data.DataMappers.toEntity
import com.vbshkn.ikollect.data.local.datasource.ArtistLocalDataSource
import com.vbshkn.ikollect.data.local.model.pojo.ArtistMinimalDetail
import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.model.pojo.GroupWithMembers
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.data.remote.datasource.ArtistRemoteDataSource
import com.vbshkn.ikollect.domain.model.ArtistOverview
import com.vbshkn.ikollect.domain.model.ArtistProfileData
import com.vbshkn.ikollect.util.asLocalResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ArtistRepository @Inject constructor(
    private val artistLocalDS: ArtistLocalDataSource,
    private val artistRemoteDS: ArtistRemoteDataSource
) {
    fun getAll(): Flow<List<ArtistEntity>> {
        return artistLocalDS.getAll()
    }

    fun getById(id: Long): Flow<ArtistEntity?> {
        return artistLocalDS.getById(id)
    }

    fun getGroupWithMembers(groupId: Long): Flow<GroupWithMembers?> {
        return artistLocalDS.getGroupWithMembers(groupId)
    }

    fun getArtistOverviews(): Flow<NetworkResult<List<ArtistOverview>>> {
        return artistLocalDS
            .getArtistOverviews()
            .asLocalResult { list -> list.map { it.toDomain() } }
    }

    fun getArtistProfileData(id: Long): Flow<NetworkResult<ArtistProfileData?>> {
        return artistLocalDS.getWithFullDetail(id).asLocalResult { it?.toDomain() }
    }

    suspend fun insertToDatabase(entity: ArtistEntity) {
        artistLocalDS.insert(entity)
    }

    suspend fun addGroupMembers(
        groupId: Long,
        memberIds: List<Long>
    ) {
        val possibleGroup = getById(groupId).first()
        if (possibleGroup?.isGroup == false) { return }

        coroutineScope {
            memberIds.map { memberId ->
                async {
                    val memberResponse = artistRemoteDS.getArtistDetails(memberId)
                    if (memberResponse.isSuccessful) {
                        val memberData = memberResponse.body()
                        memberData?.let { artistLocalDS.insertAndLinkToGroup(it.toEntity(), groupId) }
                    }
                }
            }
        }.awaitAll()
    }
}