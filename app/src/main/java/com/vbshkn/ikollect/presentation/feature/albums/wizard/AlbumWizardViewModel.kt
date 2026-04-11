package com.vbshkn.ikollect.presentation.feature.albums.wizard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vbshkn.ikollect.domain.model.candidate.AlbumCandidate
import com.vbshkn.ikollect.presentation.navigation.AlbumCandidateType
import com.vbshkn.ikollect.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf
import com.vbshkn.ikollect.domain.usecase.SaveAlbumUseCase
import com.vbshkn.ikollect.domain.base.BaseViewModel

@HiltViewModel
class AlbumWizardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saveAlbumUseCase: SaveAlbumUseCase
) : BaseViewModel<
        AlbumWizardUIState,
        AlbumWizardContract.Event,
        AlbumWizardContract.Effect
        >(initialState = AlbumWizardUIState()) {
    private val args = savedStateHandle.toRoute<Route.AlbumWizard>(
        typeMap = mapOf(typeOf<AlbumCandidate>() to AlbumCandidateType)
    )

    init {
        updateState { state -> state.copy(albumCandidate = args.candidate) }
    }

    override fun onEvent(event: AlbumWizardContract.Event) {
        when (event) {
            is AlbumWizardContract.Event.OnBackClicked -> {
                sendEffect(AlbumWizardContract.Effect.NavigateBack)
            }
            is AlbumWizardContract.Event.OnNextClicked -> {
                sendEffect(AlbumWizardContract.Effect.NavigateNext)
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
            is AlbumWizardContract.Event.OnStepChanged -> updateState {
                it.copy(stepIndex = event.newStep)
            }
            is AlbumWizardContract.Event.OnNewAlbumPrevirew -> updateState {
                it.copy(
                    albumCoverPreviews = it.albumCoverPreviews + event.uri,
                    coverImage = event.uri
                )
            }
            is AlbumWizardContract.Event.OnVersionSelected -> updateState {
                it.copy(
                    versionCandidate = event.candidate,
                    coverImage = event.candidate.coverImage
                )
            }
            is AlbumWizardContract.Event.OnAlbumPreviewSelected -> updateState { state ->
                state.copy(coverImage = event.path)
            }
            is AlbumWizardContract.Event.OnVersionNameChanged -> updateState { state ->
                state.versionCandidate?.let { candidate ->
                    state.copy(versionCandidate = candidate.copy(name = event.newName))
                } ?: state
            }
            is AlbumWizardContract.Event.OnKomcaCodeChanged -> updateState {
                it.copy(komcaNumber = event.newCode)
            }
            is AlbumWizardContract.Event.OnUserNotesChanged -> updateState {
                it.copy(albumCandidate = it.albumCandidate?.copy(userNote = event.newValue))
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
            is AlbumWizardContract.Event.OnShowKomcaHint -> {
                showDialog(AlbumWizardDialogState.AboutKomcaWizardDialog)
            }
            is AlbumWizardContract.Event.OnShowCameraRationale -> {
                showDialog(AlbumWizardDialogState.CameraRationaleWizardDialog)
            }
            AlbumWizardContract.Event.OnWrapUp -> {
                viewModelScope.launch {
                    saveAlbumUseCase(uiState.value)
                    sendEffect(AlbumWizardContract.Effect.Exit)
                }
            }
        }
    }

    private fun showDialog(dialogState: AlbumWizardDialogState) {
        updateState { it.copy(dialogState = dialogState) }
    }

    private fun dismissDialog() {
        updateState { it.copy(dialogState = AlbumWizardDialogState.None) }
    }
}
