package com.vbshkn.ikollect.presentation.feature.photocards.profile

sealed class PhotocardProfileDialogState {
    object ConfirmDeletion : PhotocardProfileDialogState()
    object None : PhotocardProfileDialogState()
}