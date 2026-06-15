package com.vbshkn.ikollect.domain.repository

import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.TagItem
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    fun getAll(): Flow<NetworkResult<List<TagItem>>>

    suspend fun insert(tagItem: TagItem)

    suspend fun update(tagItem: TagItem)

    suspend fun delete(tagItem: TagItem)

    suspend fun linkPhotocard(
        photocardId: Long,
        tagIds: List<Long>,
    )

    suspend fun updateLinks(
        photocardId: Long,
        oldTagIds: List<Long>,
        newTagIds: List<Long>
    )
}