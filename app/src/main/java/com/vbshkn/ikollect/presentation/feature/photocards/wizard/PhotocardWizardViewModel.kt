package com.vbshkn.ikollect.presentation.feature.photocards.wizard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.usecase.GetAllTagsUseCase
import com.vbshkn.ikollect.domain.usecase.GetArtistAlbumListUseCase
import com.vbshkn.ikollect.domain.usecase.GetArtistListUseCase
import com.vbshkn.ikollect.domain.usecase.GetGroupMembersUseCase
import com.vbshkn.ikollect.domain.usecase.SavePhotocardUseCase
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
    private val getArtistListUseCase: GetArtistListUseCase,
    private val getGroupMembersUseCase: GetGroupMembersUseCase,
    private val getArtistAlbumListUseCase: GetArtistAlbumListUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val savePhotocardUseCase: SavePhotocardUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PhotocardWizardUIState())
    val uiState = _uiState.asStateFlow()
    private val _effects = Channel<PhotocardWizardContract.Effect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        observeArtists()
        observeMembers()
        observeAlbums()
        observeTags()
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

            is PhotocardWizardContract.Event.OnMemberSelected -> _uiState.update { state ->
                val displayNameTemp = state.members
                    .filter { it.artistId in event.ids }
                    .joinToString { it.name } + " -"
                state.copy(
                    photocardCandidate = state.photocardCandidate.copy(
                        depictedArtistsId = event.ids,
                        displayName = displayNameTemp
                    )
                )
            }

            is PhotocardWizardContract.Event.OnOwnerSelected -> _uiState.update {
                it.copy(
                    photocardCandidate = it.photocardCandidate.copy(
                        ownerId = event.id,
                        isOwnerAGroup = event.isGroup,
                        depictedArtistsId = if (!event.isGroup) listOf(event.id) else emptyList()
                    )
                )
            }

            is PhotocardWizardContract.Event.OnAlbumSelected -> _uiState.update {
                it.copy(photocardCandidate = it.photocardCandidate.copy(albumId = event.id))
            }

            is PhotocardWizardContract.Event.OnDisplayedNameChanged -> _uiState.update {
                it.copy(photocardCandidate = it.photocardCandidate.copy(displayName = event.newName))
            }

            is PhotocardWizardContract.Event.OnAddTagClicked -> _uiState.update {
                it.copy(enableTagSelector = true)
            }
            is PhotocardWizardContract.Event.OnDismissTagSelector -> _uiState.update {
                it.copy(enableTagSelector = false)
            }
            is PhotocardWizardContract.Event.OnTagSelected -> _uiState.update { state ->
                state.copy(
                    photocardCandidate = state.photocardCandidate.copy(
                        tagIds = if (event.tagId in state.photocardCandidate.tagIds) {
                            state.photocardCandidate.tagIds - event.tagId
                        } else { state.photocardCandidate.tagIds + event.tagId }
                    )
                )
            }

            is PhotocardWizardContract.Event.OnUserNotesChanged -> _uiState.update { state ->
                state.copy(photocardCandidate = state.photocardCandidate.copy(userNote = event.newValue))
            }

            PhotocardWizardContract.Event.OnFinish -> {
                savePhotocardUseCase(uiState.value.photocardCandidate)
            }
        }
    }

    private fun sendEffect(effect: PhotocardWizardContract.Effect) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }

    private fun observeArtists() = viewModelScope.launch {
        getArtistListUseCase().collect { result ->
            when (result) {
                is NetworkResult.Loading -> _uiState.update {
                    it.copy(isLoading = true)
                }
                is NetworkResult.Success -> _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        artists = result.data.sortedBy { it.name }
                    )
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeMembers() = viewModelScope.launch {
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeAlbums() = viewModelScope.launch {
        uiState.map { it.photocardCandidate.ownerId }
            .distinctUntilChanged()
            .flatMapLatest { ownerId ->
                if (ownerId == null) { flowOf(NetworkResult.Success(emptyList())) }
                else { getArtistAlbumListUseCase(ownerId) }
            }.collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.update {
                        it.copy(isLoading = true)
                    }
                    is NetworkResult.Success -> _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            albums = result.data.sortedBy { it.name }
                        )
                    }
                    is NetworkResult.Error -> _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
    }

    private fun observeTags() = viewModelScope.launch {
        getAllTagsUseCase().collect { result ->
            when (result) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        tags = result.data
                    )
                }
                is NetworkResult.Loading -> _uiState.update {
                    it.copy(isLoading = true)
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }
}