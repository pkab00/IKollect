package com.vbshkn.ikollect.presentation.feature.photocards.wizard

sealed class PhotocardWizardDialogState {
    object ExitDialog : PhotocardWizardDialogState()
    object CameraRationale : PhotocardWizardDialogState()
    object SelectArtistTip : PhotocardWizardDialogState()
    object None : PhotocardWizardDialogState()
}