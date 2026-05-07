package com.vbshkn.ikollect.presentation.feature.albums.list

import com.vbshkn.ikollect.domain.error.AppError
import com.vbshkn.ikollect.domain.model.details.AlbumDetails
import com.vbshkn.ikollect.domain.model.candidate.AlbumCandidate

data class AlbumsUIState(
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val error: AppError? = null,
    val albums: List<AlbumDetails> = emptyList(),
    val scannedCandidate: AlbumCandidate? = null,
    val dialogState: AlbumsDialogState = AlbumsDialogState.None
)