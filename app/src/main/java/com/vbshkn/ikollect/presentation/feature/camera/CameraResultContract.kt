package com.vbshkn.ikollect.presentation.feature.camera

sealed interface CameraResultContract {
    companion object {
        const val CAMERA_RESULT: String = "camera_result"
        const val SCANNER_RESULT = "scanner_result"
    }
}