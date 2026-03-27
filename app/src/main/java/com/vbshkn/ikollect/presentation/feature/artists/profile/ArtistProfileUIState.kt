package com.vbshkn.ikollect.presentation.feature.artists.profile

import com.vbshkn.ikollect.data.AppError
import com.vbshkn.ikollect.domain.model.ArtistProfileData

data class ArtistProfileUIState(
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val profileData: ArtistProfileData? = null
)
