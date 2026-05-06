package com.vbshkn.ikollect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.vbshkn.ikollect.data.local.model.pojo.ArtistMinimalDetail
import com.vbshkn.ikollect.data.local.model.entity.ArtistArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.model.pojo.ArtistFullDetail
import com.vbshkn.ikollect.data.local.model.pojo.GroupWithMembers
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {
    @Query("SELECT * FROM ArtistEntity WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAll(): Flow<List<ArtistEntity>>

    @Query("SELECT * FROM ArtistEntity WHERE isSynchronized = 0")
    fun getUnSynchronized(): Flow<List<ArtistEntity>>

    @Query("SELECT * FROM ArtistEntity WHERE artistId = :id and isDeleted = 0 LIMIT 1")
    fun getById(id: Long): Flow<ArtistEntity?>

    @Transaction
    @Query("SELECT * FROM ArtistEntity WHERE artistId = :groupId AND isDeleted = 0")
    fun getGroupWithMembers(groupId: Long): Flow<GroupWithMembers?>

    @Transaction
    @Query("SELECT * FROM ArtistEntity WHERE artistId = :id AND isDeleted = 0")
    fun getWithFullDetail(id: Long): Flow<ArtistFullDetail?>

    @Query("""
            SELECT *, 
            (SELECT COUNT(*) FROM AlbumArtistCrossRef 
                WHERE artistId = ArtistEntity.artistId AND isDeleted = 0) 
                AS albumsCount,
            (SELECT COUNT(*) FROM PhotocardEntity 
                WHERE ownerId = ArtistEntity.artistId AND isDeleted = 0) 
                AS photocardsOwnedCount,
            (SELECT COUNT(*) FROM PhotocardArtistCrossRef 
                WHERE artistId = ArtistEntity.artistId AND isDeleted = 0)
                AS photocardsDepictedCount
            FROM ArtistEntity
            """)
    fun getArtistOverviews(): Flow<List<ArtistMinimalDetail>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(artistEntity: ArtistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<ArtistEntity>)

    @Update
    suspend fun updateAll(entities: List<ArtistEntity>)

    @Upsert
    suspend fun upsertAll(entities: List<ArtistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGroupLinks(links: List<ArtistArtistCrossRef>)

    @Transaction
    suspend fun insertAndLinkToGroup(
        member: ArtistEntity,
        groupId: Long
    ) {
        insert(member)
        upsertGroupLinks(listOf(ArtistArtistCrossRef(groupId, member.artistId, false)))
    }

    @Query("DELETE FROM ArtistEntity")
    suspend fun clearAll()

    @Query("DELETE FROM ArtistEntity WHERE isDeleted = 1")
    suspend fun clearDeleted()
}