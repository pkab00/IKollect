package com.vbshkn.ikollect.presentation.feature.addalbum

sealed class AddAlbumDialogState {
    object ConfirmExitDialog : AddAlbumDialogState()
    object CameraRationaleDialog : AddAlbumDialogState()
    object CameraErrorDialog : AddAlbumDialogState()
    object AboutKomcaDialog : AddAlbumDialogState()
    object None : AddAlbumDialogState()
}
