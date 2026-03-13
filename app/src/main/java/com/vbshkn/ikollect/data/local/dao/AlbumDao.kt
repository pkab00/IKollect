package com.vbshkn.ikollect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.vbshkn.ikollect.data.local.entity.AlbumArtistCrossRef
import com.vbshkn.ikollect.data.local.entity.AlbumEntity
import com.vbshkn.ikollect.data.local.pojo.AlbumFullDetail
import com.vbshkn.ikollect.data.local.pojo.AlbumWithArtists
import com.vbshkn.ikollect.data.local.pojo.AlbumWithPhotocards
import com.vbshkn.ikollect.data.local.pojo.ArtistWithAlbums
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Query("SELECT * FROM AlbumEntity")
    fun getAll(): Flow<List<AlbumEntity>>

    @Transaction
    @Query("SELECT * FROM AlbumEntity")
    fun getAllWithArtists(): Flow<List<AlbumWithArtists>>

    @Transaction
    @Query("SELECT * FROM AlbumEntity")
    fun getAllWithPhotocards(): Flow<List<AlbumWithPhotocards>>

    @Transaction
    @Query("SELECT * FROM AlbumEntity")
    fun getAllWithFullDetail(): Flow<List<AlbumFullDetail>>

    @Transaction
    @Query("SELECT * FROM ArtistEntity WHERE artistId == :artistId")
    fun getAllByArtist(artistId: Long): Flow<ArtistWithAlbums?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(albumEntity: AlbumEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtistLinks(links: List<AlbumArtistCrossRef>)

    @Transaction
    suspend fun insertAlbumWithArtists(
        albumEntity: AlbumEntity,
        artistIds: List<Long>
    ) {
        insertAlbum(albumEntity)
        val links = artistIds.map { artistId ->
            AlbumArtistCrossRef(albumId = albumEntity.albumId, artistId = artistId)
        }
        insertArtistLinks(links)
    }
}