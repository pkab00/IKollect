package com.vbshkn.ikollect.presentation.feature.albums

import com.vbshkn.ikollect.data.AppError
import com.vbshkn.ikollect.domain.model.Album
import com.vbshkn.ikollect.domain.model.AlbumCandidate

data class AlbumsUIState(
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val albums: List<Album> = emptyList(),
    val scannedCandidate: AlbumCandidate? = null,
    val dialogState: AlbumsDialogState = AlbumsDialogState.None
)
