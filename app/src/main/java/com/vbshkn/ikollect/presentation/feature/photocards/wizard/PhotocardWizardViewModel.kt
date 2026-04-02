package com.vbshkn.ikollect.presentation.feature.photocards.wizard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PhotocardWizardViewModel @Inject constructor(

) : ViewModel() {
    private val _uiState = MutableStateFlow(PhotocardWizardUIState())
    val uiState = _uiState.asStateFlow()
    private val _effects = Channel<PhotocardWizardContract.Effect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: PhotocardWizardContract.Event) {
        when (event) {
            is PhotocardWizardContract.Event.OnStepChanged -> _uiState.update {
                it.copy(currentStep = event.newStep)
            }
            is PhotocardWizardContract.Event.OnDismissDialog -> _uiState.update {
                it.copy(dialogState = PhotocardWizardDialogState.None)
            }
            is PhotocardWizardContract.Event.OnShowCameraRationale -> _uiState.update {
                it.copy(dialogState = PhotocardWizardDialogState.CameraRationale)
            }
            is PhotocardWizardContract.Event.OnExitClicked -> _uiState.update {
                it.copy(dialogState = PhotocardWizardDialogState.ExitDialog)
            }
            is PhotocardWizardContract.Event.OnBackClicked -> {
                sendEffect(PhotocardWizardContract.Effect.NavigateBack)
            }
            is PhotocardWizardContract.Event.OnExitConfirmed -> {
                sendEffect(PhotocardWizardContract.Effect.Exit)
            }
            is PhotocardWizardContract.Event.OnNextClicked -> {
                sendEffect(PhotocardWizardContract.Effect.NavigateNext)
            }
            is PhotocardWizardContract.Event.OnOpenGallerySelector -> {
                sendEffect(PhotocardWizardContract.Effect.OpenGallery)
            }
            is PhotocardWizardContract.Event.OnOpenCamera -> {
                sendEffect(PhotocardWizardContract.Effect.TryOpenCamera)
            }
            is PhotocardWizardContract.Event.OnPhotoSelected -> _uiState.update {
                it.copy(photocardCandidate = it.photocardCandidate.copy(imageUrl = event.uri))
            }
        }
    }

    private fun sendEffect(effect: PhotocardWizardContract.Effect) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }
}