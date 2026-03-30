package com.vbshkn.ikollect.presentation.feature.albumwizard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vbshkn.ikollect.domain.model.AlbumCandidate
import com.vbshkn.ikollect.presentation.navigation.AlbumCandidateType
import com.vbshkn.ikollect.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf
import com.vbshkn.ikollect.domain.usecase.SaveAlbumUseCase

@HiltViewModel
class AlbumWizardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saveAlbumUseCase: SaveAlbumUseCase
) : ViewModel() {
    private val args = savedStateHandle.toRoute<Route.AlbumWizardRoute>(
        typeMap = mapOf(typeOf<AlbumCandidate>() to AlbumCandidateType)
    )
    private val _uiState = MutableStateFlow(AlbumWizardUIState(albumCandidate = args.candidate))
    val uiState = _uiState.asStateFlow()
    private val _effects = Channel<AlbumWizardContract.Effect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()


    fun onEvent(event: AlbumWizardContract.Event) {
        when (event) {
            is AlbumWizardContract.Event.OnBackClicked -> {
                sendEffect(AlbumWizardContract.Effect.NavigateBack)
            }
            is AlbumWizardContract.Event.OnNextClicked -> {
                sendEffect(AlbumWizardContract.Effect.NavigateNext)
            }
            is AlbumWizardContract.Event.OnExitClicked -> {
                showDialog(AlbumWizardDialogState.ConfirmExitWizardDialog)
            }
            is AlbumWizardContract.Event.OnExitConfirmed -> {
                sendEffect(AlbumWizardContract.Effect.Exit)
            }
            is AlbumWizardContract.Event.OnDismissDialog -> {
                dismissDialog()
            }
            is AlbumWizardContract.Event.OnSelectPicture -> {
                sendEffect(AlbumWizardContract.Effect.OpenGallery)
            }
            is AlbumWizardContract.Event.OnTakePicture -> {
                sendEffect(AlbumWizardContract.Effect.TryOpenCamera)
            }
            is AlbumWizardContract.Event.OnScanKomca -> {
                sendEffect(AlbumWizardContract.Effect.TryOpenScanner)
            }
            AlbumWizardContract.Event.OnShowKomcaHint -> {
                showDialog(AlbumWizardDialogState.AboutKomcaWizardDialog)
            }
            is AlbumWizardContract.Event.OnShowCameraRationale -> {
                showDialog(AlbumWizardDialogState.CameraRationaleWizardDialog)
            }
            is AlbumWizardContract.Event.OnPictureCaptured -> {
                _uiState.update {
                    it.copy(
                        versionCandidate = it.versionCandidate?.copy(coverImage = event.uri),
                        isCoverCached = true
                    )
                }
            }
            is AlbumWizardContract.Event.OnUpdateVersion -> {
                _uiState.update {
                    it.copy(versionCandidate = event.candidate)
                }
            }
            is AlbumWizardContract.Event.OnExistingPhotoSelected -> {
                _uiState.value.versionCandidate?.let { candidate ->
                    _uiState.update {
                        it.copy(
                            versionCandidate = candidate.copy(coverImage = event.path),
                            isCoverCached = false
                        )
                    }
                }
            }
            is AlbumWizardContract.Event.OnVersionNameChanged -> {
                _uiState.value.versionCandidate?.let { candidate ->
                    _uiState.update {
                        it.copy(versionCandidate = candidate.copy(name = event.newName))
                    }
                }
            }
            is AlbumWizardContract.Event.OnKomcaCodeChanged -> {
                _uiState.update {
                    it.copy(komcaNumber = event.newCode)
                }
            }
            is AlbumWizardContract.Event.OnUserNotesChanged -> {
                _uiState.update {
                    it.copy(
                        albumCandidate = it.albumCandidate.copy(userNote = event.newValue)
                    )
                }
            }
            AlbumWizardContract.Event.OnWrapUp -> viewModelScope.launch {
                saveAlbumUseCase(uiState.value)
            }
        }
    }

    fun canNavigateBack(currentRoute: Route.AlbumWizardFlow): Boolean {
        return when (currentRoute) {
            Route.AlbumWizardFlow.SeeInfo -> false
            else -> true
        }
    }

    fun canNavigateNext(currentRoute: Route.AlbumWizardFlow): Boolean {
        return when (currentRoute) {
            Route.AlbumWizardFlow.SeeInfo -> true
            Route.AlbumWizardFlow.SelectVersion -> _uiState.value.versionCandidate != null
            Route.AlbumWizardFlow.DetailsWizard -> {
                val candidate = _uiState.value.versionCandidate
                candidate?.coverImage != null && candidate.name.isNotBlank()
            }
            Route.AlbumWizardFlow.WrapUp -> true
        }
    }

    private fun showDialog(dialogState: AlbumWizardDialogState) {
        _uiState.update {
            it.copy(dialogState = dialogState)
        }
    }

    private fun dismissDialog() {
        _uiState.update {
            it.copy(dialogState = AlbumWizardDialogState.None)
        }
    }

    private fun sendEffect(effect: AlbumWizardContract.Effect) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }
}
