package com.vbshkn.ikollect.data.repository

import androidx.room.withTransaction
import com.vbshkn.ikollect.data.local.database.AppDatabase
import com.vbshkn.ikollect.data.local.datasource.TagLocalDataSource
import com.vbshkn.ikollect.data.local.model.entity.PhotocardTagCrossRef
import com.vbshkn.ikollect.data.mapper.DataMappers.toDomain
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.TagItem
import com.vbshkn.ikollect.util.asLocalResult
import com.vbshkn.ikollect.util.now
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TagRepository @Inject constructor(
    private val tagLocalDS: TagLocalDataSource,
    private val db: AppDatabase
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
            PhotocardTagCrossRef(photocardId, tagId, false, updatedAt = now())
        }
        tagLocalDS.insertTagLinks(tagRelations)
    }
    
    suspend fun updateLinks(
        photocardId: Long,
        oldTagIds: List<Long>,
        newTagIds: List<Long>
    ) {
        db.withTransaction {
            val toDelete = oldTagIds.filter { it !in newTagIds }
            val toInsert = newTagIds.filter { it !in oldTagIds }

            toDelete.forEach { tagId ->
                tagLocalDS.deleteLink(PhotocardTagCrossRef(photocardId, tagId))
            }

            if (toInsert.isNotEmpty()) {
                val insertRelations = toInsert.map { PhotocardTagCrossRef(photocardId, it) }
                tagLocalDS.insertTagLinks(insertRelations)
            }
        }
    }
}