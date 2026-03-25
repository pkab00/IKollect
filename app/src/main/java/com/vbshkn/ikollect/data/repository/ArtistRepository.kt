package com.vbshkn.ikollect.data.repository

import com.vbshkn.ikollect.data.local.datasource.ArtistLocalDataSource
import com.vbshkn.ikollect.data.local.entity.ArtistEntity
import javax.inject.Inject

class ArtistRepository @Inject constructor(
    private val artistLocalDS: ArtistLocalDataSource
) {
    suspend fun insertToDatabase(entity: ArtistEntity) {
        artistLocalDS.insert(entity)
    }
}