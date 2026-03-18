package com.vbshkn.ikollect.data

sealed interface AppError {
    object ScanningFailed : AppError
    object ReleaseNotFound : AppError
    object InvalidAlbumStyle : AppError
    object ConnectionFailed : AppError
    object UnknownError : AppError
    data class LocalDataLoadingError(val message: String) : AppError
}