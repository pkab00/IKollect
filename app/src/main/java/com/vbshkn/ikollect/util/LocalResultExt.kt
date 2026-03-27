package com.vbshkn.ikollect.util

import com.vbshkn.ikollect.data.AppError
import com.vbshkn.ikollect.data.remote.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun <T> Flow<T>.asLocalResult(): Flow<NetworkResult<T>> {
    return this
        .map { data ->
            NetworkResult.Success(data) as NetworkResult<T>
        }
        .onStart {
            emit(NetworkResult.Loading)
        }
        .catch { e ->
            val errorMessage = e.localizedMessage ?: "Unknown Local Error"
            emit(NetworkResult.Error(AppError.LocalDataLoadingError(errorMessage)))
        }
}

fun <T, R> Flow<T>.asLocalResult(
    transform: (T) -> R
): Flow<NetworkResult<R>> {
    return this
        .map { data ->
            NetworkResult.Success(transform(data)) as NetworkResult<R>
        }
        .onStart {
            emit(NetworkResult.Loading)
        }
        .catch { e ->
            val errorMessage = e.localizedMessage ?: "Unknown Local Error"
            emit(NetworkResult.Error(AppError.LocalDataLoadingError(errorMessage)))
        }
}