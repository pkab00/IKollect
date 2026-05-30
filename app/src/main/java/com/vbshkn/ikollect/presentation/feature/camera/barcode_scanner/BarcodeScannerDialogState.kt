package com.vbshkn.ikollect.presentation.feature.camera.barcode_scanner

import com.vbshkn.ikollect.util.UiText

sealed class BarcodeScannerDialogState {
    data object SuccessDialog : BarcodeScannerDialogState()
    data class ErrorDialog(val message: UiText) : BarcodeScannerDialogState()
    data object None : BarcodeScannerDialogState()
}
