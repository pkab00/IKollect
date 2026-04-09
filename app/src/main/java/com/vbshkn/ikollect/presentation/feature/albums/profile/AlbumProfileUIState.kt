package com.vbshkn.ikollect.presentation.feature.albums.profile

import com.vbshkn.ikollect.data.AppError
import com.vbshkn.ikollect.domain.model.profile.AlbumProfileData

data class AlbumProfileUIState(
    val profile: AlbumProfileData? = null,
    val isLoading: Boolean = false,
    val error: AppError? = null
)
