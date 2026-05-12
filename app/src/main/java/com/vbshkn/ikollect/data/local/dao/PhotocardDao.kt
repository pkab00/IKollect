package com.vbshkn.ikollect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.vbshkn.ikollect.data.local.model.entity.PhotocardArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.PhotocardEntity
import com.vbshkn.ikollect.data.local.model.pojo.ArtistWithPhotocards
import com.vbshkn.ikollect.data.local.model.pojo.PhotocardFullDetail
import com.vbshkn.ikollect.data.local.model.pojo.PhotocardMinimalDetail
import com.vbshkn.ikollect.util.now
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotocardDao {
    @Query("SELECT * FROM PhotocardEntity WHERE isDeleted = 0")
    fun getAll(): Flow<List<PhotocardEntity>>

    @Query("SELECT * FROM PhotocardEntity WHERE isDeleted = 0 AND isFavorite = 1")
    fun getFavorite(): Flow<List<PhotocardEntity>>

    @Query("SELECT * FROM PhotocardEntity WHERE isSynchronized = 0")
    fun getUnSynchronized(): Flow<List<PhotocardEntity>>

    @Query("SELECT * FROM PhotocardEntity WHERE photocardId = :id AND isDeleted = 0")
    fun getById(id: Long): Flow<PhotocardEntity?>

    @Transaction
    @Query("SELECT * FROM PhotocardEntity WHERE isDeleted = 0")
    fun getAllWithArtists(): Flow<List<PhotocardMinimalDetail>>

    @Transaction
    @Query("SELECT * FROM ArtistEntity WHERE artistId = :artistId AND isDeleted = 0")
    fun getAllByArtist(artistId: Long): Flow<ArtistWithPhotocards?>

    @Transaction
    @Query("SELECT * FROM PhotocardEntity WHERE photocardId = :id AND isDeleted = 0")
    fun getWithFullDetail(id: Long): Flow<PhotocardFullDetail?>

    @Update
    suspend fun updatePhotocard(photocardEntity: PhotocardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotocard(photocardEntity: PhotocardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<PhotocardEntity>)

    @Update
    suspend fun updateAll(entities: List<PhotocardEntity>)

    @Upsert
    suspend fun upsertAll(entities: List<PhotocardEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertArtistLinks(links: List<PhotocardArtistCrossRef>)

    @Transaction
    suspend fun insertPhotocardWithArtists(
        photocardEntity: PhotocardEntity,
        artistIds: List<Long>
    ): Long {
        val photocardId = insertPhotocard(photocardEntity)
        val links = artistIds.map { artistId ->
            PhotocardArtistCrossRef(photocardId = photocardId, artistId = artistId, isSynchronized = false)
        }
        upsertArtistLinks(links)
        return photocardId
    }

    @Query(
        """
            UPDATE PhotocardEntity SET
            isDeleted = 1,
            isSynchronized = 0,
            updatedAt = :time
            WHERE photocardId = :id
        """
    )
    suspend fun setDeleted(id: Long, time: Long = now())

    @Query(
        """
            UPDATE PhotocardEntity SET
            isFavorite = :isFavorite,
            isSynchronized = 0,
            updatedAt = :time
            WHERE photocardId = :id
        """
    )
    suspend fun setFavorite(id: Long, isFavorite: Boolean, time: Long = now())

    @Query("DELETE FROM PhotocardEntity")
    suspend fun clearAll()

    @Query("DELETE FROM PhotocardEntity WHERE isDeleted = 1")
    suspend fun clearDeleted()
}