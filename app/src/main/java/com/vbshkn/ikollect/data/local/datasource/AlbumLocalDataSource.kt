package com.vbshkn.ikollect.data.local.datasource

import com.vbshkn.ikollect.data.local.dao.AlbumDao
import com.vbshkn.ikollect.data.local.model.entity.AlbumEntity
import com.vbshkn.ikollect.data.local.model.pojo.AlbumFullDetail
import com.vbshkn.ikollect.data.local.model.pojo.AlbumWithArtists
import com.vbshkn.ikollect.data.local.model.pojo.AlbumWithPhotocards
import com.vbshkn.ikollect.data.local.model.pojo.ArtistWithAlbums
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlbumLocalDataSource @Inject constructor(
    private val dao: AlbumDao
) {
    fun getAll(): Flow<List<AlbumEntity>> {
        return dao.getAll()
    }

    fun getAllWithArtists(): Flow<List<AlbumWithArtists>> {
        return dao.getAllWithArtists()
    }

    fun getAllByArtist(artistId: Long): Flow<List<ArtistWithAlbums>> {
        return dao.getAllByArtist(artistId)
    }

    fun getWithArtistsByBarcode(barcode: String): Flow<AlbumWithArtists>? {
        return dao.getWithArtistsByBarcode(barcode)
    }

    fun getAllWithPhotocards(): Flow<List<AlbumWithPhotocards>> {
        return dao.getAllWithPhotocards()
    }

    fun getWithFullDetail(id: Long): Flow<AlbumFullDetail?> {
        return dao.getWithFullDetail(id)
    }

    fun getAllWithFullDetail(): Flow<List<AlbumFullDetail>> {
        return dao.getAllWithFullDetail()
    }

    suspend fun insertAlbumWithArtists(
        albumEntity: AlbumEntity,
        artistIds: List<Long>
    ) {
        dao.insertAlbumWithArtists(albumEntity, artistIds)
    }
}