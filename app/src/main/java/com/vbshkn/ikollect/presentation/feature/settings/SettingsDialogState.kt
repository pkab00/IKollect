package com.vbshkn.ikollect.presentation.feature.settings

sealed class SettingsDialogState {
    object ConfirmLogOutDialog : SettingsDialogState()
    object NewNicknameDialog : SettingsDialogState()
    object None : SettingsDialogState()
}