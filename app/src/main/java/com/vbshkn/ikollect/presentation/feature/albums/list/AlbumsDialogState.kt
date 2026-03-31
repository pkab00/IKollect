package com.vbshkn.ikollect.presentation.feature.albums.list

import com.vbshkn.ikollect.util.UiText

sealed class AlbumsDialogState {
    data class ScanningResultDialog(val message: String) : AlbumsDialogState()
    data class ScanningErrorDialog(val errorMessage: UiText) : AlbumsDialogState()
    object None : AlbumsDialogState()
}