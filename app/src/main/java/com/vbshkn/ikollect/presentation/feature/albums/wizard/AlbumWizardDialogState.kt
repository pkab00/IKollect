package com.vbshkn.ikollect.presentation.feature.albums.wizard

sealed class AlbumWizardDialogState {
    object ConfirmExitWizardDialog : AlbumWizardDialogState()
    object CameraRationaleWizardDialog : AlbumWizardDialogState()
    object CameraErrorWizardDialog : AlbumWizardDialogState()
    object AboutKomcaWizardDialog : AlbumWizardDialogState()
    object None : AlbumWizardDialogState()
}
