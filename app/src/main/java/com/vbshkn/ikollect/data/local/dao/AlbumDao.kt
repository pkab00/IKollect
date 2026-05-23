package com.vbshkn.ikollect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.vbshkn.ikollect.data.local.model.entity.AlbumArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.AlbumEntity
import com.vbshkn.ikollect.data.local.model.pojo.AlbumFullDetail
import com.vbshkn.ikollect.data.local.model.pojo.AlbumWithArtists
import com.vbshkn.ikollect.data.local.model.pojo.AlbumWithPhotocards
import com.vbshkn.ikollect.data.local.model.pojo.ArtistWithAlbums
import com.vbshkn.ikollect.util.now
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Query("SELECT * FROM AlbumEntity WHERE isDeleted = 0")
    fun getAll(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM AlbumEntity WHERE isSynchronized = 0")
    fun getUnSynchronized(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM AlbumEntity WHERE isDeleted = 0")
    fun getDeleted(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM AlbumEntity WHERE albumId = :id AND isDeleted = 0")
    fun getById(id: Long): Flow<AlbumEntity?>

    @Transaction
    @Query("SELECT * FROM AlbumEntity WHERE isDeleted = 0")
    fun getAllWithArtists(): Flow<List<AlbumWithArtists>>

    @Transaction
    @Query("SELECT * FROM AlbumEntity WHERE isDeleted = 0 AND isFavorite = 1")
    fun getFavoriteWithArtists(): Flow<List<AlbumWithArtists>>

    @Transaction
    @Query("SELECT * FROM AlbumEntity WHERE barcodeNumber = :barcode AND isDeleted = 0")
    fun getWithArtistsByBarcode(barcode: String): Flow<AlbumWithArtists>?

    @Transaction
    @Query("SELECT * FROM AlbumEntity WHERE isDeleted = 0")
    fun getAllWithPhotocards(): Flow<List<AlbumWithPhotocards>>

    @Transaction
    @Query("SELECT * FROM AlbumEntity WHERE isDeleted = 0")
    fun getAllWithFullDetail(): Flow<List<AlbumFullDetail>>

    @Transaction
    @Query("SELECT * FROM ArtistEntity WHERE artistId == :artistId AND isDeleted = 0")
    fun getAllByArtist(artistId: Long): Flow<List<ArtistWithAlbums>>

    @Transaction
    @Query("SELECT * FROM AlbumEntity WHERE albumId == :id AND isDeleted = 0")
    fun getWithFullDetail(id: Long): Flow<AlbumFullDetail?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<AlbumEntity>)

    @Update
    suspend fun updateAll(entities: List<AlbumEntity>)

    @Upsert
    suspend fun upsertAll(entities: List<AlbumEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(albumEntity: AlbumEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertArtistLinks(links: List<AlbumArtistCrossRef>)

    @Transaction
    suspend fun insertAlbumWithArtists(
        albumEntity: AlbumEntity,
        artistIds: List<Long>
    ) : Long {
        val generatedId = insertAlbum(albumEntity)
        val links = artistIds.map { artistId ->
            AlbumArtistCrossRef(albumId = generatedId, artistId = artistId, isSynchronized = false)
        }
        upsertArtistLinks(links)
        return generatedId
    }

    @Update
    suspend fun updateAlbum(albumEntity: AlbumEntity)

    @Query(
        """
            UPDATE AlbumEntity SET
            isDeleted = 1,
            isSynchronized = 0,
            updatedAt = :time
            WHERE albumId = :id
        """
    )
    suspend fun setDeleted(id: Long, time: Long = now())

    @Query(
        """
            UPDATE AlbumEntity SET
            isFavorite = :isFavorite,
            isSynchronized = 0,
            updatedAt = :time
            WHERE albumId = :id
        """
    )
    suspend fun setFavorite(id: Long, isFavorite: Boolean, time: Long = now())

    @Query("DELETE FROM AlbumEntity")
    suspend fun clearAll()

    @Query("DELETE FROM AlbumEntity WHERE isDeleted = 1")
    suspend fun clearDeleted()
}