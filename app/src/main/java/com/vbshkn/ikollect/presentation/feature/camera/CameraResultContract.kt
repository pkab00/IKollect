package com.vbshkn.ikollect.presentation.feature.camera

sealed interface CameraResultContract {
    companion object {
        const val PHOTO_CAMERA_RESULT: String = "camera_result"
        const val KOMCA_SCANNER_RESULT = "scanner_result"
    }
}