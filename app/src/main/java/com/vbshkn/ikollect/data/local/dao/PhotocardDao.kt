package com.vbshkn.ikollect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.vbshkn.ikollect.data.local.entity.PhotocardArtistCrossRef
import com.vbshkn.ikollect.data.local.entity.PhotocardEntity
import com.vbshkn.ikollect.data.local.pojo.ArtistWithPhotocards
import com.vbshkn.ikollect.data.local.pojo.PhotocardWithArtists
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotocardDao {
    @Query("SELECT * FROM PhotocardEntity")
    fun getAll(): Flow<List<PhotocardEntity>>

    @Transaction
    @Query("SELECT * FROM PhotocardEntity")
    fun getAllWithArtists(): Flow<List<PhotocardWithArtists>>

    @Transaction
    @Query("SELECT * FROM ArtistEntity WHERE id = :artistId")
    fun getAllByArtist(artistId: Long): Flow<ArtistWithPhotocards?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotocard(photocardEntity: PhotocardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtistLinks(links: List<PhotocardArtistCrossRef>)

    @Transaction
    suspend fun insertPhotocardWithArtists(
        photocardEntity: PhotocardEntity,
        artistIds: List<Long>
    ) {
        insertPhotocard(photocardEntity)
        val links = artistIds.map { artistId ->
            PhotocardArtistCrossRef(photocardId = photocardEntity.id, artistId = artistId)
        }
        insertArtistLinks(links)
    }
}