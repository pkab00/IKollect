package com.vbshkn.ikollect.presentation.feature.albums.profile

sealed class AlbumProfileDialogState {
    object ConfirmDeletion : AlbumProfileDialogState()
    object None : AlbumProfileDialogState()
}