package com.vbshkn.ikollect.presentation.feature.photocards.wizard

import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.usecase.GetAllTagsUseCase
import com.vbshkn.ikollect.domain.usecase.GetArtistAlbumListUseCase
import com.vbshkn.ikollect.domain.usecase.GetArtistListUseCase
import com.vbshkn.ikollect.domain.usecase.GetGroupMembersUseCase
import com.vbshkn.ikollect.domain.usecase.SavePhotocardUseCase
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.presentation.theme.inversePrimaryDark
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PhotocardWizardViewModel @Inject constructor(
    private val getArtistListUseCase: GetArtistListUseCase,
    private val getGroupMembersUseCase: GetGroupMembersUseCase,
    private val getArtistAlbumListUseCase: GetArtistAlbumListUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val savePhotocardUseCase: SavePhotocardUseCase
) : BaseViewModel<
        PhotocardWizardUIState,
        PhotocardWizardContract.Event,
        PhotocardWizardContract.Effect
        >(initialState = PhotocardWizardUIState()) {
    init {
        observeArtists()
        observeMembers()
        observeAlbums()
        observeTags()
    }

    override fun onEvent(event: PhotocardWizardContract.Event) {
        when (event) {
            is PhotocardWizardContract.Event.OnStepChanged -> updateState {
                it.copy(currentStep = event.newStep)
            }

            is PhotocardWizardContract.Event.OnDismissDialog -> updateState {
                it.copy(dialogState = PhotocardWizardDialogState.None)
            }

            is PhotocardWizardContract.Event.OnShowCameraRationale -> updateState {
                it.copy(dialogState = PhotocardWizardDialogState.CameraRationale)
            }

            is PhotocardWizardContract.Event.OnExitClicked -> updateState {
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

            is PhotocardWizardContract.Event.OnPhotoSelected -> updateState {
                it.copy(
                    photocardImagePreviews = it.photocardImagePreviews + event.uri,
                    photocardCandidate = it.photocardCandidate.copy(imageUrl = event.uri)
                )
            }

            is PhotocardWizardContract.Event.OnPhotocardPreviewSelected -> updateState {
                it.copy(photocardCandidate = it.photocardCandidate.copy(imageUrl = event.url))
            }

            is PhotocardWizardContract.Event.OnShowSelectArtistTip -> updateState {
                it.copy(dialogState = PhotocardWizardDialogState.SelectArtistTip)
            }

            is PhotocardWizardContract.Event.OnMemberSelected -> updateState { state ->
                val displayNameTemp = state.members
                    .filter { it.artistId in event.ids }
                    .joinToString { it.name } + " - "
                state.copy(
                    photocardCandidate = state.photocardCandidate.copy(
                        depictedArtistsId = event.ids,
                        displayName = displayNameTemp
                    )
                )
            }

            is PhotocardWizardContract.Event.OnOwnerSelected -> updateState { state ->
                val artistName = uiState.value.artists
                    .find { it.artistId == event.id }
                    ?.name ?: ""
                state.copy(
                    photocardCandidate = state.photocardCandidate.copy(
                        displayName = if (!event.isGroup) "$artistName - " else "",
                        ownerId = event.id,
                        isOwnerAGroup = event.isGroup,
                        depictedArtistsId = if (!event.isGroup) listOf(event.id) else emptyList()
                    )
                )
            }

            is PhotocardWizardContract.Event.OnAlbumSelected -> updateState {
                it.copy(photocardCandidate = it.photocardCandidate.copy(albumId = event.id))
            }

            is PhotocardWizardContract.Event.OnDisplayedNameChanged -> updateState {
                it.copy(photocardCandidate = it.photocardCandidate.copy(displayName = event.newName))
            }

            is PhotocardWizardContract.Event.OnAddTagClicked -> updateState {
                it.copy(enableTagSelector = true)
            }
            is PhotocardWizardContract.Event.OnDismissTagSelector -> updateState {
                it.copy(enableTagSelector = false)
            }
            is PhotocardWizardContract.Event.OnTagSelected -> updateState { state ->
                state.copy(
                    photocardCandidate = state.photocardCandidate.copy(
                        tagIds = if (event.tagId in state.photocardCandidate.tagIds) {
                            state.photocardCandidate.tagIds - event.tagId
                        } else { state.photocardCandidate.tagIds + event.tagId }
                    )
                )
            }

            is PhotocardWizardContract.Event.OnUserNotesChanged -> updateState { state ->
                state.copy(photocardCandidate = state.photocardCandidate.copy(userNote = event.newValue))
            }

            is PhotocardWizardContract.Event.OnFinish -> {
                savePhotocardUseCase(uiState.value.photocardCandidate)
            }
        }
    }

    private fun observeArtists() = collectFlowIntoState(
        flow = getArtistListUseCase(),
        onSuccess = {state, data -> state.copy(artists = data.sortedBy { it.name }, isLoading = false)},
        onLoading = {state -> state.copy(isLoading = true)},
        onError = {state, e -> state.copy(isLoading = false)}
    )


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeMembers() = collectFlowIntoState(
            flow = uiState
                .map { it.photocardCandidate.ownerId }
                .distinctUntilChanged()
                .flatMapLatest { getGroupMembersUseCase(it) },
            onSuccess = { state, data -> state.copy(members = data.sortedBy { it.name }, isLoading = false) },
            onLoading = { state -> state.copy(isLoading = true) },
            onError = { state, e -> state.copy(isLoading = false) }
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeAlbums() = collectFlowIntoState(
            flow = uiState
                .map { it.photocardCandidate.ownerId }
                .distinctUntilChanged()
                .flatMapLatest { getArtistAlbumListUseCase(it) },
            onSuccess = { state, data -> state.copy(albums = data.sortedBy { it.timestamp }, isLoading = false) },
            onLoading = { state -> state.copy(isLoading = true) },
            onError = { state, e -> state.copy(isLoading = false) }
        )

    private fun observeTags() = collectFlowIntoState(
        flow = getAllTagsUseCase(),
        onSuccess = {state, data -> state.copy(tags = data, isLoading = false)},
        onLoading = {state -> state.copy(isLoading = true)},
        onError = {state, e -> state.copy(isLoading = false)}
    )
}