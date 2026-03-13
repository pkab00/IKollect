package com.vbshkn.ikollect.data.remote

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val code: Int? = null, val message: String? = null) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}