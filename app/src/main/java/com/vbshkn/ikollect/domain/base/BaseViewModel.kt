package com.vbshkn.ikollect.domain.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.domain.error.AppError
import com.vbshkn.ikollect.data.remote.NetworkResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<S, EV, EF> (initialState: S): ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()
    private val _effects = Channel<EF>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    abstract fun onEvent(event: EV)

    protected fun updateState(transform: (S) -> S) {
        _uiState.update(transform)
    }

     protected fun sendEffect(effect: EF) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }

    protected fun <T> collectFlowIntoState(
        flow: Flow<NetworkResult<T>>,
        onSuccess: (S, T) -> S,
        onLoading: (S) -> S,
        onError: (S, AppError) -> S
    ) = viewModelScope.launch {
        flow.collect { result ->
            when (result) {
                is NetworkResult.Error -> updateState {onError(it, result.error) }
                is NetworkResult.Loading -> updateState(onLoading)
                is NetworkResult.Success -> updateState { onSuccess(it, result.data) }
            }
        }
    }
}