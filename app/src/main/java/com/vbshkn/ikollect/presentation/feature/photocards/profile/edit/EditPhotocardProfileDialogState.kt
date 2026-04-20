package com.vbshkn.ikollect.presentation.feature.photocards.profile.edit

sealed class EditPhotocardProfileDialogState {
    object CameraRationale : EditPhotocardProfileDialogState()
    object None : EditPhotocardProfileDialogState()
}

