package com.vbshkn.ikollect.domain.error

sealed interface AppError {
    object ProfileNotFound : AppError
    object ScanningFailed : AppError
    object ReleaseNotFound : AppError
    object InvalidAlbumStyle : AppError
    object ConnectionFailed : AppError
    object UnknownError : AppError
    data class LocalDataLoadingError(val message: String) : AppError
}