package com.vbshkn.ikollect.presentation.feature.photocards.profile

import com.vbshkn.ikollect.domain.error.AppError
import com.vbshkn.ikollect.domain.model.profile.PhotocardProfileData

data class PhotocardProfileUIState(
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val error: AppError? = null,
    val dialogState: PhotocardProfileDialogState = PhotocardProfileDialogState.None,
    val profile: PhotocardProfileData? = null
)
