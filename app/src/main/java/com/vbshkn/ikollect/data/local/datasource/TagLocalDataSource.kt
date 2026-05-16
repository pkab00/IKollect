package com.vbshkn.ikollect.data.local.datasource

import com.vbshkn.ikollect.data.local.dao.TagDao
import com.vbshkn.ikollect.data.local.model.entity.PhotocardTagCrossRef
import com.vbshkn.ikollect.data.local.model.entity.TagEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TagLocalDataSource @Inject constructor(
    private val dao: TagDao
) {
    fun getAll(): Flow<List<TagEntity>> {
        return dao.getAll()
    }

    suspend fun insert(entity: TagEntity) {
        dao.insert(entity)
    }

    suspend fun insertTagLinks(links: List<PhotocardTagCrossRef>) {
        dao.upsertTagLinks(links)
    }

    suspend fun deleteLink(link: PhotocardTagCrossRef) {
        dao.deleteLink(link)
    }

    suspend fun clearAllButSystem() {
        dao.clearUserTags()
    }
}