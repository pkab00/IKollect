package com.vbshkn.ikollect.presentation.feature.artists.search

import com.vbshkn.ikollect.domain.model.list.ArtistListItem
import com.vbshkn.ikollect.domain.usecase.get.GetAllArtistsUseCase
import com.vbshkn.ikollect.presentation.feature.search.AbstractSearchViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class ArtistSearchViewModel @Inject constructor(
    private val getAllArtistsUseCase: GetAllArtistsUseCase
) : AbstractSearchViewModel<ArtistListItem>() {
    init {
        observeSearchResults(getAllArtistsUseCase())
    }
}