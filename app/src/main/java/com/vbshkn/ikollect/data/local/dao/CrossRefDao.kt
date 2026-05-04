package com.vbshkn.ikollect.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.vbshkn.ikollect.data.local.model.entity.AlbumArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.ArtistArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.PhotocardArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.PhotocardTagCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface CrossRefDao {
    @Query("SELECT * FROM AlbumArtistCrossRef")
    fun getAlbumArtist(): Flow<List<AlbumArtistCrossRef>>

    @Query("SELECT * FROM ArtistArtistCrossRef")
    fun getArtistArtist(): Flow<List<ArtistArtistCrossRef>>

    @Query("SELECT * FROM PhotocardArtistCrossRef")
    fun getPhotocardArtist(): Flow<List<PhotocardArtistCrossRef>>

    @Query("SELECT * FROM PhotocardTagCrossRef")
    fun getPhotocardTag(): Flow<List<PhotocardTagCrossRef>>

    @Update
    suspend fun updateAlbumArtist(updated: List<AlbumArtistCrossRef>)

    @Update
    suspend fun updateArtistArtist(updated: List<ArtistArtistCrossRef>)

    @Update
    suspend fun updatePhotocardArtist(updated: List<PhotocardArtistCrossRef>)

    @Update
    suspend fun updatePhotocardTag(updated: List<PhotocardTagCrossRef>)

    @Query("DELETE FROM AlbumArtistCrossRef")
    suspend fun clearAlbumArtist()

    @Query("DELETE FROM ArtistArtistCrossRef")
    suspend fun clearArtistArtist()

    @Query("DELETE FROM PhotocardArtistCrossRef")
    suspend fun clearPhotocardArtist()

    @Query("DELETE FROM PhotocardTagCrossRef")
    suspend fun clearPhotocardTag()

    @Transaction
    suspend fun clearAll() {
        clearAlbumArtist()
        clearArtistArtist()
        clearPhotocardArtist()
        clearPhotocardTag()
    }
}