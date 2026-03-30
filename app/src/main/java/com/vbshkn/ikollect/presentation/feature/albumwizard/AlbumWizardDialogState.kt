package com.vbshkn.ikollect.presentation.feature.albumwizard

sealed class AlbumWizardDialogState {
    object ConfirmExitWizardDialog : AlbumWizardDialogState()
    object CameraRationaleWizardDialog : AlbumWizardDialogState()
    object CameraErrorWizardDialog : AlbumWizardDialogState()
    object AboutKomcaWizardDialog : AlbumWizardDialogState()
    object None : AlbumWizardDialogState()
}
