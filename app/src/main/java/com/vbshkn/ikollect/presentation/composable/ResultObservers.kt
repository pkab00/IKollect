package com.vbshkn.ikollect.presentation.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.domain.model.UserItemImage
import com.vbshkn.ikollect.presentation.feature.camera.CameraResultContract

@Composable
fun CameraResultObserver(
    savedStateHandle: SavedStateHandle,
    onResult: (UserItemImage) -> Unit
) {
    val cameraResult by savedStateHandle
        .getStateFlow<String?>(CameraResultContract.PHOTO_CAMERA_RESULT, null)
        .collectAsStateWithLifecycle()

    LaunchedEffect(cameraResult) {
        if (cameraResult != null) {
            onResult(UserItemImage(uri = cameraResult!!, isCached = true))
            savedStateHandle[CameraResultContract.PHOTO_CAMERA_RESULT] = null
        }
    }
}

@Composable
fun ScannerResultObserver(
    savedStateHandle: SavedStateHandle,
    onResult: (String) -> Unit
) {
    val scannerResult by savedStateHandle
        .getStateFlow<String?>(CameraResultContract.KOMCA_SCANNER_RESULT, null)
        .collectAsStateWithLifecycle()

    LaunchedEffect(scannerResult) {
        if (scannerResult != null) {
            onResult(scannerResult!!)
            savedStateHandle[CameraResultContract.KOMCA_SCANNER_RESULT] = null
        }
    }
}