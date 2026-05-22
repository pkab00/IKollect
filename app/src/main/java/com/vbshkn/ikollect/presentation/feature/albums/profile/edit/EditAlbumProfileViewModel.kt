package com.vbshkn.ikollect.presentation.feature.albums.profile.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.model.UserItemImage
import com.vbshkn.ikollect.domain.usecase.get.GetAlbumProfileDataUseCase
import com.vbshkn.ikollect.domain.usecase.update.UpdateAlbumUseCase
import com.vbshkn.ikollect.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditAlbumProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAlbumProfileDataUseCase: GetAlbumProfileDataUseCase,
    private val updateAlbumUseCase: UpdateAlbumUseCase
) : BaseViewModel<
        EditAlbumProfileUIState,
        EditAlbumProfileContract.Event,
        EditAlbumProfileContract.Effect
        >(initialState = EditAlbumProfileUIState()) {
    private val args = savedStateHandle.toRoute<Route.AlbumFlow.Edit>()
    private val albumId = args.id

    init {
        observeProfile()
    }

    override fun onEvent(event: EditAlbumProfileContract.Event) {
        when (event) {
            is EditAlbumProfileContract.Event.OnBackClicked -> {
                sendEffect(EditAlbumProfileContract.Effect.NavigateBack)
            }

            is EditAlbumProfileContract.Event.OnOpenGalleryClicked -> {
                sendEffect(EditAlbumProfileContract.Effect.OpenGallery)
            }

            is EditAlbumProfileContract.Event.OnSaveChangesClicked -> viewModelScope.launch {
                updateAlbumUseCase(
                    id = albumId,
                    name = uiState.value.albumName,
                    version = uiState.value.albumVersion,
                    komcaNumber = uiState.value.komcaNumber,
                    userNotes = uiState.value.userNotes,
                    image = uiState.value.image,
                    oldImage = uiState.value.oldImageUrl
                )
                sendEffect(EditAlbumProfileContract.Effect.NavigateBack)
            }

            is EditAlbumProfileContract.Event.OnAlbumNameChanged -> {
                updateState { it.copy(albumName = event.name) }
            }

            is EditAlbumProfileContract.Event.OnKomcaNumberChanged -> {
                updateState { it.copy(komcaNumber = event.number) }
            }

            is EditAlbumProfileContract.Event.OnUserNotesChanged -> {
                updateState { it.copy(userNotes = event.notes) }
            }

            is EditAlbumProfileContract.Event.OnAlbumVersionChanged -> {
                updateState { it.copy(albumVersion = event.version) }
            }

            is EditAlbumProfileContract.Event.OnKomcaScannerClicked -> {
                sendEffect(EditAlbumProfileContract.Effect.TryOpenScanner)
            }

            is EditAlbumProfileContract.Event.OnShowCameraRationale -> {
                updateState { it.copy(dialogState = EditAlbumProfileDialogState.CameraRationale) }
            }

            is EditAlbumProfileContract.Event.OnDismissDialog -> {
                updateState { it.copy(dialogState = EditAlbumProfileDialogState.None) }
            }

            is EditAlbumProfileContract.Event.OnImageChanged -> {
                updateState { it.copy(image = event.image) }
            }

            EditAlbumProfileContract.Event.OnOpenCameraClicked -> {
                sendEffect(EditAlbumProfileContract.Effect.TryOpenCamera)
            }
        }
    }

    private fun observeProfile() = collectFlowIntoState(
        flow = getAlbumProfileDataUseCase(albumId),
        onLoading = { state -> state.copy(isLoading = true) },
        onSuccess = { state, data ->
            state.copy(
                isLoading = false,
                image = UserItemImage(uri = data?.album?.coverImage ?: "", isCached = false),
                oldImageUrl = data?.album?.coverImage ?: "",
                albumName = data?.album?.name ?: "",
                albumVersion = data?.album?.version ?: "",
                komcaNumber = data?.album?.komcaNumber ?: "",
                userNotes = data?.album?.userNote ?: ""
            )
        },
        onError = { state, e -> state.copy(isLoading = false, error = e) }
    )
}
