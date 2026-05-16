package com.vbshkn.ikollect.presentation.feature.settings

import com.vbshkn.ikollect.domain.error.ValidationError
import com.vbshkn.ikollect.domain.model.AppUser

data class SettingsUIState(
    val user: AppUser? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val dialogState: SettingsDialogState = SettingsDialogState.None,

    val newNickname: String? = null,
    val nicknameValidationError: ValidationError.NicknameError? = null
)
