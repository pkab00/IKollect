package com.vbshkn.ikollect.presentation.feature.camera.barcode_scanner

import com.vbshkn.ikollect.domain.model.candidate.AlbumCandidate

data class BarcodeScannerUiState(
    val barcode: String? = null,
    val albumCandidate: AlbumCandidate? = null,
    val dialogState: BarcodeScannerDialogState = BarcodeScannerDialogState.None,
    val isLoading: Boolean = false
)
