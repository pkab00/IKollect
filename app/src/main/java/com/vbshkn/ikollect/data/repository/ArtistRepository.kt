package com.vbshkn.ikollect.data.repository

import com.vbshkn.ikollect.data.DataMappers.toEntity
import com.vbshkn.ikollect.data.local.datasource.ArtistLocalDataSource
import com.vbshkn.ikollect.data.local.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.pojo.GroupWithMembers
import com.vbshkn.ikollect.data.remote.datasource.ArtistRemoteDataSource
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