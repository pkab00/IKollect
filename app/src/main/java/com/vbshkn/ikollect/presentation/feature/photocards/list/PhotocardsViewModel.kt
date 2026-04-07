package com.vbshkn.ikollect.presentation.feature.photocards.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.usecase.GetAllPhotocardsUseCase
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsContract
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PhotocardsViewModel @Inject constructor(
    private val getAllPhotocardsUseCase: GetAllPhotocardsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PhotocardsUIState())
    val uiState = _uiState.asStateFlow()
    private val _effects = Channel<PhotocardsContract.Effect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            collectPhotocards()
        }
    }

    fun omEvent(event: PhotocardsContract.Event) {
        when (event) {
            is PhotocardsContract.Event.OnWizardClicked -> {
                sendEffect(PhotocardsContract.Effect.GoToWizard)
            }
            is PhotocardsContract.Event.OnPhotocardPreviewPressed -> _uiState.update {
                it.copy(fullScreenPreview = event.imageUrl)
            }
            is PhotocardsContract.Event.OnPhotocardPreviewReleased -> _uiState.update {
                it.copy(fullScreenPreview = null)

            }
        }
    }

    private fun sendEffect(effect: PhotocardsContract.Effect) {
        viewModelScope.launch {
            _effects.send(effect)
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