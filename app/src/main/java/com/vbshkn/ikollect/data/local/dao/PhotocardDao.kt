package com.vbshkn.ikollect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.vbshkn.ikollect.data.local.model.entity.PhotocardArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.PhotocardEntity
import com.vbshkn.ikollect.data.local.model.pojo.ArtistWithPhotocards
import com.vbshkn.ikollect.data.local.model.pojo.PhotocardFullDetail
import com.vbshkn.ikollect.data.local.model.pojo.PhotocardMinimalDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotocardDao {
    @Query("SELECT * FROM PhotocardEntity")
    fun getAll(): Flow<List<PhotocardEntity>>

    @Transaction
    @Query("SELECT * FROM PhotocardEntity")
    fun getAllWithArtists(): Flow<List<PhotocardMinimalDetail>>

    @Transaction
    @Query("SELECT * FROM ArtistEntity WHERE artistId = :artistId")
    fun getAllByArtist(artistId: Long): Flow<ArtistWithPhotocards?>

    @Transaction
    @Query("SELECT * FROM PhotocardEntity WHERE photocardId = :id")
    fun getWithFullDetail(id: Long): Flow<PhotocardFullDetail?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotocard(photocardEntity: PhotocardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtistLinks(links: List<PhotocardArtistCrossRef>)

    @Transaction
    suspend fun insertPhotocardWithArtists(
        photocardEntity: PhotocardEntity,
        artistIds: List<Long>
    ): Long {
        val photocardId = insertPhotocard(photocardEntity)
        val links = artistIds.map { artistId ->
            PhotocardArtistCrossRef(photocardId = photocardId, artistId = artistId)
        }
        insertArtistLinks(links)
        return photocardId
    }
}