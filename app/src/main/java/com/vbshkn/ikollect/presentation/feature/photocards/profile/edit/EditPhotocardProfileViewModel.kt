package com.vbshkn.ikollect.presentation.feature.photocards.profile.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.model.UserItemImage
import com.vbshkn.ikollect.domain.usecase.get.GetAllTagsUseCase
import com.vbshkn.ikollect.domain.usecase.get.GetPhotocardProfileDataUseCase
import com.vbshkn.ikollect.domain.usecase.update.UpdatePhotocardUseCase
import com.vbshkn.ikollect.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPhotocardProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPhotocardProfileDataUseCase: GetPhotocardProfileDataUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val updatePhotocardUseCase: UpdatePhotocardUseCase
) : BaseViewModel<
        EditPhotocardProfileUIState,
        EditPhotocardProfileContract.Event,
        EditPhotocardProfileContract.Effect
        >(initialState = EditPhotocardProfileUIState()) {
    private val args = savedStateHandle.toRoute<Route.PhotocardFlow.Edit>()
    private val photocardId = args.id

    init {
        observeProfile()
        observeTags()
    }

    override fun onEvent(event: EditPhotocardProfileContract.Event) {
        when (event) {
            is EditPhotocardProfileContract.Event.OnBackClicked -> {
                sendEffect(EditPhotocardProfileContract.Effect.NavigateBack)
            }

            is EditPhotocardProfileContract.Event.OnOpenGalleryClicked -> {
                sendEffect(EditPhotocardProfileContract.Effect.OpenGallery)
            }

            is EditPhotocardProfileContract.Event.OnOpenCameraClicked -> {
                sendEffect(EditPhotocardProfileContract.Effect.TryOpenCamera)
            }

            is EditPhotocardProfileContract.Event.OnSaveChangesClicked -> viewModelScope.launch {
                updatePhotocardUseCase(
                    id = photocardId,
                    image = uiState.value.image,
                    oldImage = uiState.value.oldImageUrl,
                    photocardName = uiState.value.photocardName,
                    userNotes = uiState.value.userNotes,
                    oldTagIds = uiState.value.oldTagIds,
                    selectedTagIds = uiState.value.selectedTagIds
                )
                sendEffect(EditPhotocardProfileContract.Effect.NavigateBack)
            }

            is EditPhotocardProfileContract.Event.OnPhotocardNameChanged -> {
                updateState { it.copy(photocardName = event.name) }
            }

            is EditPhotocardProfileContract.Event.OnUserNotesChanged -> {
                updateState { it.copy(userNotes = event.notes) }
            }

            is EditPhotocardProfileContract.Event.OnTagClick -> {
                updateState {
                    val newSelectedTags = it.selectedTagIds.toMutableSet()
                    if (event.tagId in newSelectedTags) {
                        newSelectedTags.remove(event.tagId)
                    } else {
                        newSelectedTags.add(event.tagId)
                    }
                    it.copy(selectedTagIds = newSelectedTags)
                }
            }

            is EditPhotocardProfileContract.Event.OnShowCameraRationale -> {
                updateState { it.copy(dialogState = EditPhotocardProfileDialogState.CameraRationale) }
            }

            is EditPhotocardProfileContract.Event.OnDismissDialog -> {
                updateState { it.copy(dialogState = EditPhotocardProfileDialogState.None) }
            }

            is EditPhotocardProfileContract.Event.OnImageChanged -> {
                updateState { it.copy(image = event.image) }
            }
            is EditPhotocardProfileContract.Event.OnDismissTagSelector -> {
                updateState { it.copy(enableTagSelector = false) }
            }
            is EditPhotocardProfileContract.Event.OnSelectTagsClick -> {
                updateState { it.copy(enableTagSelector = true) }
            }
        }
    }

    private fun observeProfile() = collectFlowIntoState(
        flow = getPhotocardProfileDataUseCase(photocardId),
        onLoading = { state -> state.copy(isLoading = true) },
        onSuccess = { state, data ->
            state.copy(
                isLoading = false,
                image = UserItemImage(
                    uri = data?.photocard?.imageUrl ?: "",
                    isCached = false
                ),
                oldImageUrl = data?.photocard?.imageUrl ?: "",
                photocardName = data?.photocard?.displayName ?: "",
                userNotes = data?.photocard?.userNotes ?: "",
                oldTagIds = data?.photocard?.tags?.map { it.id }?.toSet() ?: emptySet(),
                selectedTagIds = data?.photocard?.tags?.map { it.id }?.toSet() ?: emptySet()
            )
        },
        onError = { state, e -> state.copy(isLoading = false, error = e) }
    )

    private fun observeTags() = collectFlowIntoState(
        flow = getAllTagsUseCase(),
        onLoading = { state -> state.copy(isLoading = true) },
        onSuccess = { state, data -> state.copy(isLoading = false, allTags = data) },
        onError = { state, e ->
            state.copy(
                isLoading = false, error = e
            )
        }
    )
}
