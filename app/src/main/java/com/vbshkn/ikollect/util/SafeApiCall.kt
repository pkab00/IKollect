package com.vbshkn.ikollect.util

import com.vbshkn.ikollect.domain.error.AppError
import com.vbshkn.ikollect.data.remote.NetworkResult
import retrofit2.Response

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
    return try {
        val response = apiCall()
        val body = response.body()

        if (response.isSuccessful && body != null) {
            NetworkResult.Success(body)
        } else {
            NetworkResult.Error(AppError.UnknownError)
        }
    } catch (e: Exception) {
        NetworkResult.Error(AppError.ConnectionFailed)
    }
}