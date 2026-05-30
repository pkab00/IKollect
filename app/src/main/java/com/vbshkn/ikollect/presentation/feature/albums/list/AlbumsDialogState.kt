package com.vbshkn.ikollect.presentation.feature.albums.list

sealed class AlbumsDialogState {
    data object CameraRationaleDialog : AlbumsDialogState()
    data object None : AlbumsDialogState()
}