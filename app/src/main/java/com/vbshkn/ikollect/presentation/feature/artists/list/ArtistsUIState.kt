package com.vbshkn.ikollect.presentation.feature.artists.list

import com.vbshkn.ikollect.data.AppError
import com.vbshkn.ikollect.data.local.model.pojo.ArtistMinimalDetail
import com.vbshkn.ikollect.domain.model.ArtistOverview

data class ArtistsUIState(
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val groupOverviews: List<ArtistOverview> = emptyList(),
    val soloistsOverviews: List<ArtistOverview> = emptyList()
)