package com.vbshkn.ikollect.util

import com.vbshkn.ikollect.data.remote.NetworkResult
import retrofit2.Response

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
    return try {
        val response = apiCall()
        val body = response.body()

        if (response.isSuccessful && body != null) {
            NetworkResult.Success(body)
        } else {
            NetworkResult.Error(
                code = response.code(),
                message = response.errorBody()?.string() ?: "Unknown error"
            )
        }
    } catch (e: Exception) {
        NetworkResult.Error(message = e.localizedMessage ?: "Network connection failed")
    }
}