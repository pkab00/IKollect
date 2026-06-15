package com.vbshkn.ikollect.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.vbshkn.ikollect.data.local.model.entity.PhotocardTagCrossRef
import com.vbshkn.ikollect.data.local.model.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Query("SELECT * FROM TagEntity WHERE isDeleted = 0")
    fun getAll(): Flow<List<TagEntity>>

    @Query("SELECT * FROM TagEntity WHERE isDeleted = 0")
    fun getAllShot(): List<TagEntity>

    @Query("SELECT * FROM TagEntity WHERE isSynchronized = 0 AND isSystemTag = 0")
    fun getUnSynchronizedShot(): List<TagEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<TagEntity>)

    @Update
    suspend fun update(entity: TagEntity)

    @Update
    suspend fun updateAll(entities: List<TagEntity>)

    @Upsert
    suspend fun upsertAll(entities: List<TagEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTagLinks(links: List<PhotocardTagCrossRef>)

    @Delete
    suspend fun delete(entity: TagEntity)

    @Query("UPDATE TagEntity SET isDeleted = 1 WHERE tagId = :tagId")
    suspend fun softDelete(tagId: Long)

    @Delete
    suspend fun deleteLink(link: PhotocardTagCrossRef)

    @Query("DELETE FROM tagentity")
    suspend fun clearAll()

    @Query("DELETE FROM tagentity WHERE isSystemTag = 0")
    suspend fun clearUserTags()

    @Query("DELETE FROM tagentity WHERE isDeleted = 1")
    suspend fun clearDeleted()
}