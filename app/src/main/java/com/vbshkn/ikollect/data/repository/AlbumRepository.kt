package com.vbshkn.ikollect.data.repository

import com.vbshkn.ikollect.data.local.datasource.AlbumLocalDataSource
import com.vbshkn.ikollect.data.remote.datasource.AlbumRemoteDataSource
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    private val albumRemoteDS: AlbumRemoteDataSource,
    private val albumLocalDS: AlbumLocalDataSource,
    private val artistLocalDS: AlbumLocalDataSource
) {
}