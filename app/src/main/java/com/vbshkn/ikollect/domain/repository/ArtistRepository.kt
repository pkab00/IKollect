package com.vbshkn.ikollect.domain.repository

import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.list.ArtistListItem
import com.vbshkn.ikollect.domain.model.profile.ArtistProfileData
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {
    fun getListItems(): Flow<NetworkResult<List<ArtistListItem>>>
    fun getGroupMembers(groupId: Long): Flow<NetworkResult<List<ArtistListItem>>>
    fun getFavorite(): Flow<NetworkResult<List<ArtistListItem>>>
    fun getArtistProfile(id: Long): Flow<NetworkResult<ArtistProfileData?>>

    suspend fun insertToDatabase(entity: ArtistEntity)

    suspend fun addGroupMembers(
        groupId: Long,
        memberIds: List<Long>
    )

    suspend fun toggleFavorite(id: Long)
}