package com.vbshkn.ikollect.presentation.feature.artists.search

import com.vbshkn.ikollect.domain.model.list.ArtistListItem
import com.vbshkn.ikollect.domain.usecase.get.GetArtistListUseCase
import com.vbshkn.ikollect.presentation.feature.search.AbstractSearchViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class ArtistSearchViewModel @Inject constructor(
    private val getArtistListUseCase: GetArtistListUseCase
) : AbstractSearchViewModel<ArtistListItem>() {
    init {
        observeSearchResults(getArtistListUseCase())
    }
}