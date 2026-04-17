package com.vbshkn.ikollect.presentation.feature.albums.profile.edit

sealed class EditAlbumProfileDialogState {
    object CameraRationale : EditAlbumProfileDialogState()
    object None : EditAlbumProfileDialogState()
}

