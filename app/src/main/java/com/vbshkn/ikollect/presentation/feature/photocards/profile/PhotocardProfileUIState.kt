package com.vbshkn.ikollect.presentation.feature.photocards.profile

import com.vbshkn.ikollect.data.AppError
import com.vbshkn.ikollect.domain.model.profile.PhotocardProfileData

data class PhotocardProfileUIState(
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val profile: PhotocardProfileData? = null
)
