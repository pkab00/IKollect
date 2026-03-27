package com.vbshkn.ikollect.presentation.feature.artists

import com.vbshkn.ikollect.data.AppError
import com.vbshkn.ikollect.data.local.model.ArtistOverview

data class ArtistsUIState(
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val groupOverviews: List<ArtistOverview> = emptyList(),
    val soloistsOverviews: List<ArtistOverview> = emptyList()
)
