package com.vbshkn.ikollect.presentation.feature.photocards.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.usecase.GetAllPhotocardsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PhotocardsViewModel @Inject constructor(
    private val getAllPhotocardsUseCase: GetAllPhotocardsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PhotocardsUIState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            collectPhotocards()
        }
    }

    private suspend fun collectPhotocards()
        = getAllPhotocardsUseCase().collect { networkResult ->
        when(networkResult) {
            is NetworkResult.Loading -> _uiState.update {
                it.copy(isLoading = false)
            }
            is NetworkResult.Success -> _uiState.update {
                it.copy(
                    isLoading = false,
                    photocards = networkResult.data
                )
            }
            is NetworkResult.Error -> _uiState.update {
                it.copy(
                    isLoading = false,
                    error = networkResult.error
                )
            }
        }
    }
}