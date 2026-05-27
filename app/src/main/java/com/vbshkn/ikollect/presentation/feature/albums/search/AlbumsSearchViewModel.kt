package com.vbshkn.ikollect.presentation.feature.albums.search

import com.vbshkn.ikollect.domain.model.details.AlbumDetails
import com.vbshkn.ikollect.domain.usecase.get.GetAllAlbumsUseCase
import com.vbshkn.ikollect.presentation.feature.search.AbstractSearchViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class AlbumsSearchViewModel @Inject constructor(
    private val getAllAlbumsUseCase: GetAllAlbumsUseCase
) : AbstractSearchViewModel<AlbumDetails>() {
    init {
        observeSearchResults(getAllAlbumsUseCase())
    }
}