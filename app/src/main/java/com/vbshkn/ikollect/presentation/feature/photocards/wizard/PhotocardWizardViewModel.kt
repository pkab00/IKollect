package com.vbshkn.ikollect.presentation.feature.photocards.wizard

import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.usecase.get.GetAllTagsUseCase
import com.vbshkn.ikollect.domain.usecase.get.GetAllAlbumsByArtistUseCase
import com.vbshkn.ikollect.domain.usecase.get.GetAllArtistsUseCase
import com.vbshkn.ikollect.domain.usecase.get.GetGroupMembersUseCase
import com.vbshkn.ikollect.domain.usecase.save.SavePhotocardUseCase
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.business.ArtistFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@HiltViewModel
class PhotocardWizardViewModel @Inject constructor(
    private val getAllArtistsUseCase: GetAllArtistsUseCase,
    private val getGroupMembersUseCase: GetGroupMembersUseCase,
    private val getAllAlbumsByArtistUseCase: GetAllAlbumsByArtistUseCase,
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
                    photocardImagePreviews = it.photocardImagePreviews + event.photo,
                    photocardCandidate = it.photocardCandidate.copy(image = event.photo)
                )
            }

            is PhotocardWizardContract.Event.OnPhotocardPreviewSelected -> updateState {
                it.copy(photocardCandidate = it.photocardCandidate.copy(image = event.preview))
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
                        } else {
                            state.photocardCandidate.tagIds + event.tagId
                        }
                    )
                )
            }

            is PhotocardWizardContract.Event.OnUserNotesChanged -> updateState { state ->
                state.copy(photocardCandidate = state.photocardCandidate.copy(userNote = event.newValue))
            }

            is PhotocardWizardContract.Event.OnFinish -> {
                savePhotocardUseCase(uiState.value.photocardCandidate)
            }

            is PhotocardWizardContract.Event.OnArtistFilterSelected -> {
                if (uiState.value.artistFilter != event.filter) {
                    updateState { it.copy(artistFilter = event.filter) }
                } else { updateState { it.copy(artistFilter = ArtistFilter.ALL) } }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeArtists() = collectFlowIntoState(
        flow = combine(getAllArtistsUseCase(), uiState) {
            artists, _ -> artists
        }.flatMapLatest { result ->
            when (result) {
                is NetworkResult.Success -> {
                    val filteredGroups = when (uiState.value.artistFilter) {
                        ArtistFilter.GROUPS -> result.data.filter { it.isGroup }
                        ArtistFilter.SOLOISTS -> result.data.filter { !it.isGroup }
                        ArtistFilter.ALL -> result.data
                    }
                    flowOf(NetworkResult.Success(filteredGroups))
                } else -> flowOf(result)
            }
        },
        onSuccess = { state, data -> state.copy(artists = data.sortedBy { it.name }, isLoading = false) },
        onLoading = { state -> state.copy(isLoading = true) },
        onError = { state, e -> state.copy(isLoading = false) }
    )


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeMembers() = collectFlowIntoState(
        flow = uiState
            .map { it.photocardCandidate.ownerId }
            .distinctUntilChanged()
            .flatMapLatest { getGroupMembersUseCase(it) },
        onSuccess = { state, data ->
            state.copy(
                members = data.sortedBy { it.name },
                isLoading = false
            )
        },
        onLoading = { state -> state.copy(isLoading = true) },
        onError = { state, e -> state.copy(isLoading = false) }
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeAlbums() = collectFlowIntoState(
        flow = uiState
            .map { it.photocardCandidate.ownerId }
            .distinctUntilChanged()
            .flatMapLatest { getAllAlbumsByArtistUseCase(it) },
        onSuccess = { state, data ->
            state.copy(
                albums = data.sortedBy { it.timestamp },
                isLoading = false
            )
        },
        onLoading = { state -> state.copy(isLoading = true) },
        onError = { state, e -> state.copy(isLoading = false) }
    )

    private fun observeTags() = collectFlowIntoState(
        flow = getAllTagsUseCase(),
        onSuccess = { state, data -> state.copy(tags = data, isLoading = false) },
        onLoading = { state -> state.copy(isLoading = true) },
        onError = { state, e -> state.copy(isLoading = false) }
    )
}