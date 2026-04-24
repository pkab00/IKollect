package com.vbshkn.ikollect.presentation.feature.artists.list

import com.vbshkn.ikollect.domain.AppError
import com.vbshkn.ikollect.domain.model.list.ArtistListItem

data class ArtistsUIState(
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val groupOverviews: List<ArtistListItem> = emptyList(),
    val soloistsOverviews: List<ArtistListItem> = emptyList()
)