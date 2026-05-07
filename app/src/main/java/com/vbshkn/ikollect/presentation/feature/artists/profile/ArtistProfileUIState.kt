package com.vbshkn.ikollect.presentation.feature.artists.profile

import com.vbshkn.ikollect.domain.error.AppError
import com.vbshkn.ikollect.domain.model.profile.ArtistProfileData

data class ArtistProfileUIState(
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val error: AppError? = null,
    val profileData: ArtistProfileData? = null
)
