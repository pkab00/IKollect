package com.vbshkn.ikollect.data.repository

import com.vbshkn.ikollect.data.mapper.DataMappers.toProfile
import com.vbshkn.ikollect.data.mapper.DataMappers.toListItem
import com.vbshkn.ikollect.data.mapper.DataMappers.toEntity
import com.vbshkn.ikollect.data.local.datasource.ArtistLocalDataSource
import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.data.remote.datasource.ArtistRemoteDataSource
import com.vbshkn.ikollect.domain.model.list.ArtistListItem
import com.vbshkn.ikollect.domain.model.profile.ArtistProfileData
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

    private fun getById(id: Long): Flow<ArtistEntity?> {
        return artistLocalDS.getById(id)
    }

    fun getGroupMembers(groupId: Long): Flow<NetworkResult<List<ArtistListItem>>> {
        return artistLocalDS
            .getGroupWithMembers(groupId)
            .asLocalResult { groupWithMembers ->
                groupWithMembers?.members?.map { it.toListItem() } ?: emptyList()
            }
    }

    fun getListItems(): Flow<NetworkResult<List<ArtistListItem>>> {
        return artistLocalDS
            .getAll()
            .asLocalResult { list -> list.map { it.toListItem() } }
    }

    fun getFavorite(): Flow<NetworkResult<List<ArtistListItem>>> {
        return artistLocalDS
            .getFavorite()
            .asLocalResult { list -> list.map { it.toListItem() } }
    }

    fun getArtistProfile(id: Long): Flow<NetworkResult<ArtistProfileData?>> {
        return artistLocalDS.getWithFullDetail(id).asLocalResult { it?.toProfile() }
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

    suspend fun toggleFavorite(id: Long, oldValue: Boolean) {
        artistLocalDS.setFavorite(id, !oldValue)
    }
}