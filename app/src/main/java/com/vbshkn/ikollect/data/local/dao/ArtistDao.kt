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
import com.vbshkn.ikollect.util.now
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {
    @Query("SELECT * FROM ArtistEntity WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAll(): Flow<List<ArtistEntity>>

    @Query("SELECT * FROM ArtistEntity WHERE isGroup = 1 AND isDeleted = 0 ORDER BY name ASC")
    fun getGroups(): Flow<List<ArtistEntity>>

    @Query("SELECT * FROM ArtistEntity WHERE isGroup = 0 AND isDeleted = 0 ORDER BY name ASC")
    fun getSoloists(): Flow<List<ArtistEntity>>

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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(artistEntity: ArtistEntity)

    @Update
    suspend fun update(artistEntity: ArtistEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entities: List<ArtistEntity>)

    @Update
    suspend fun updateAll(entities: List<ArtistEntity>)

    @Upsert
    suspend fun upsertAll(entities: List<ArtistEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGroupLinks(links: List<ArtistArtistCrossRef>)

    @Transaction
    suspend fun insertAndLinkToGroup(
        member: ArtistEntity,
        groupId: Long
    ) {
        insert(member)
        insertGroupLinks(listOf(ArtistArtistCrossRef(groupId, member.artistId, false)))
    }

    @Query(
        """
            UPDATE ArtistEntity SET
            isDeleted = 1,
            isSynchronized = 0,
            updatedAt = :time
            WHERE artistId = :id
        """
    )
    suspend fun setDeleted(id: Long, time: Long = now())

    @Query(
        """
            UPDATE ArtistEntity SET
            isFavorite = :isFavorite,
            isSynchronized = 0,
            updatedAt = :time
            WHERE artistId = :id
        """
    )
    suspend fun setFavorite(id: Long, isFavorite: Boolean, time: Long = now())

    @Query("DELETE FROM ArtistEntity")
    suspend fun clearAll()

    @Query("DELETE FROM ArtistEntity WHERE artistId IN (:ids)")
    suspend fun clearSelected(ids: List<Long>)

    @Query("DELETE FROM ArtistEntity WHERE isDeleted = 1")
    suspend fun clearDeleted()

    @Query("SELECT COUNT(*) FROM ArtistArtistCrossRef WHERE groupId = :id OR memberId = :id AND isDeleted = 0")
    fun countArtistArtistCrossRefs(id: Long): Long

    @Query("SELECT COUNT(*) FROM AlbumArtistCrossRef WHERE artistId = :id AND isDeleted = 0")
    fun countAlbumArtistCrossRefs(id: Long): Long

    @Query("SELECT COUNT(*) FROM PhotocardArtistCrossRef WHERE artistId = :id AND isDeleted = 0")
    fun countPhotocardArtistCrossRefs(id: Long): Long

    @Query("SELECT COUNT(*) FROM PhotocardEntity WHERE ownerId = :id AND isDeleted = 0")
    fun countOwnedPhotocards(id: Long): Long

    @Transaction
    fun countAllOwnedItems(id: Long): Long {
        return countAlbumArtistCrossRefs(id) +
                countPhotocardArtistCrossRefs(id) +
                countOwnedPhotocards(id)
    }
}