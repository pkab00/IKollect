package com.vbshkn.ikollect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.vbshkn.ikollect.data.local.entity.ArtistArtistCrossRef
import com.vbshkn.ikollect.data.local.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.pojo.GroupWithMembers
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {
    @Query("SELECT * FROM ArtistEntity ORDER BY name ASC")
    fun getAll(): Flow<List<ArtistEntity>>

    @Query("SELECT * FROM ArtistEntity WHERE artistId = :id LIMIT 1")
    fun getById(id: Long): Flow<ArtistEntity?>

    @Transaction
    @Query("SELECT * FROM ArtistEntity WHERE artistId = :groupId")
    fun getGroupWithMembers(groupId: Long): Flow<GroupWithMembers?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(artistEntity: ArtistEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGroupLinks(links: List<ArtistArtistCrossRef>)

    @Transaction
    suspend fun insertAndLinkToGroup(
        member: ArtistEntity,
        groupId: Long
    ) {
        insert(member)
        insertGroupLinks(listOf(ArtistArtistCrossRef(groupId, member.artistId)))
    }
}