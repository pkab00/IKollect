package com.vbshkn.ikollect.domain.usecase

import com.vbshkn.ikollect.data.local.datasource.AlbumLocalDataSource
import com.vbshkn.ikollect.data.local.datasource.ArtistLocalDataSource
import com.vbshkn.ikollect.data.local.datasource.PhotocardLocalDataSource
import com.vbshkn.ikollect.data.local.datasource.TagLocalDataSource
import com.vbshkn.ikollect.data.repository.ImageRepository
import com.vbshkn.ikollect.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ClearLocalDataUseCase @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
    private val albumLocalDataSource: AlbumLocalDataSource,
    private val artistLocalDataSource: ArtistLocalDataSource,
    private val photocardLocalDataSource: PhotocardLocalDataSource,
    private val tagLocalDataSource: TagLocalDataSource,
    private val imageRepository: ImageRepository
) {
    operator fun invoke() = scope.launch {
        photocardLocalDataSource.clearAll()
        albumLocalDataSource.clearAll()
        tagLocalDataSource.clearAllButSystem()
        artistLocalDataSource.clearAll()

        imageRepository.clearLocalStorage()
    }
}