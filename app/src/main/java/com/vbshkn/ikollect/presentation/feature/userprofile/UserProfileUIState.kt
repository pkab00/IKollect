package com.vbshkn.ikollect.presentation.feature.userprofile

import com.vbshkn.ikollect.domain.error.AppError
import com.vbshkn.ikollect.domain.model.AppUser

data class UserProfileUIState(
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val user: AppUser? = null
)
