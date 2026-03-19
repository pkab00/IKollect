package com.vbshkn.ikollect.presentation.feature.addalbum

sealed class AddAlbumDialogState {
    object ConfirmExitDialog : AddAlbumDialogState()
    object None : AddAlbumDialogState()
}
