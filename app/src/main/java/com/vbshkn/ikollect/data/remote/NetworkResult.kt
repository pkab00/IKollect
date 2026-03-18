package com.vbshkn.ikollect.data.remote

import com.vbshkn.ikollect.data.AppError

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val error: AppError) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}