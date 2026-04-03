package com.vbshkn.ikollect.presentation.feature.photocards.wizard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.usecase.GetArtistOverviewsUseCase
import com.vbshkn.ikollect.domain.usecase.GetGroupMembersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PhotocardWizardViewModel @Inject constructor(
    private val getArtistOverviewsUseCase: GetArtistOverviewsUseCase,
    private val getGroupMembersUseCase: GetGroupMembersUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PhotocardWizardUIState())
    val uiState = _uiState.asStateFlow()
    private val _effects = Channel<PhotocardWizardContract.Effect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        collectArtists()
        collectMembers()
    }

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

            is PhotocardWizardContract.Event.OnShowSelectArtistTip -> _uiState.update {
                it.copy(dialogState = PhotocardWizardDialogState.SelectArtistTip)
            }

            is PhotocardWizardContract.Event.OnUpdateDepictedIds -> _uiState.update {
                it.copy(photocardCandidate = it.photocardCandidate.copy(depictedArtistsId = event.ids))
            }

            is PhotocardWizardContract.Event.OnUpdateOwner -> _uiState.update {
                it.copy(
                    photocardCandidate = it.photocardCandidate.copy(
                        ownerId = event.id,
                        isOwnerAGroup = event.isGroup,
                        depictedArtistsId = if (!event.isGroup) listOf(event.id) else emptyList()
                    )
                )
            }
        }
    }

    private fun sendEffect(effect: PhotocardWizardContract.Effect) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }

    private fun collectArtists() = viewModelScope.launch {
        getArtistOverviewsUseCase().collect { result ->
            when (result) {
                is NetworkResult.Loading -> _uiState.update {
                    it.copy(isLoading = true)
                }
                is NetworkResult.Success -> _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        artistOverviews = result.data.sortedBy { it.name }
                    )
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun collectMembers() = viewModelScope.launch {
        uiState.map { it.photocardCandidate.ownerId }
            .distinctUntilChanged()
            .flatMapLatest { ownerId ->
                if (ownerId == null) { flowOf(NetworkResult.Success(emptyList())) }
                else { getGroupMembersUseCase(ownerId) }
            }.collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.update {
                        it.copy(isLoading = true)
                    }
                    is NetworkResult.Success -> _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            members = result.data.sortedBy { it.name }
                        )
                    }
                    is NetworkResult.Error -> _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
    }
}