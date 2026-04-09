package com.vbshkn.ikollect.data.repository

import com.vbshkn.ikollect.data.local.datasource.TagLocalDataSource
import com.vbshkn.ikollect.data.local.model.entity.PhotocardTagCrossRef
import com.vbshkn.ikollect.data.mapper.DataMappers.toDomain
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.TagItem
import com.vbshkn.ikollect.util.asLocalResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TagRepository @Inject constructor(
    private val tagLocalDS: TagLocalDataSource
) {
    fun getAll(): Flow<NetworkResult<List<TagItem>>> {
        return tagLocalDS.getAll()
            .asLocalResult { list ->
                list.map { it.toDomain() }
            }
    }

    suspend fun linkPhotocard(
        photocardId: Long,
        tagIds: List<Long>,
    ) {
        val tagRelations = tagIds.map { tagId ->
            PhotocardTagCrossRef(photocardId, tagId)
        }
        tagLocalDS.insertTagLinks(tagRelations)
    }
}