package com.vbshkn.ikollect.presentation.feature.photocards.profile

import com.vbshkn.ikollect.presentation.feature.photocards.profile.PhotocardProfileContract.Event
import com.vbshkn.ikollect.presentation.feature.photocards.profile.PhotocardProfileContract.Effect
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.RefreshDataUseCase
import com.vbshkn.ikollect.domain.usecase.delete.DeletePhotocardUseCase
import com.vbshkn.ikollect.domain.usecase.favorite.ToggleFavoritePhotocardUseCase
import com.vbshkn.ikollect.domain.usecase.get.GetPhotocardProfileDataUseCase
import com.vbshkn.ikollect.presentation.feature.photocards.profile.PhotocardProfileContract.Effect.*
import com.vbshkn.ikollect.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotocardProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPhotocardProfileDataUseCase: GetPhotocardProfileDataUseCase,
    private val refreshDataUseCase: RefreshDataUseCase,
    private val toggleFavoritePhotocardUseCase: ToggleFavoritePhotocardUseCase,
    private val deletePhotocardUseCase: DeletePhotocardUseCase
) : BaseViewModel<PhotocardProfileUIState, Event, Effect>(initialState = PhotocardProfileUIState()) {
    private val args = savedStateHandle.toRoute<Route.PhotocardFlow.Profile>()
    private val photocardId = args.id

    init {
        observeProfile()
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.OnBackClicked -> {
                sendEffect(NavigateBack)
            }
            is Event.OnEditClicked -> {
                sendEffect(NavigateToEdit(photocardId))
            }
            is Event.OnOwnerCardClicked -> {
                uiState.value.profile?.photocard?.owner?.artistId?.let {
                    sendEffect(NavigateToArtist(it))
                }
            }
            is Event.OnAlbumCardClicked -> {
                uiState.value.profile?.album?.albumId?.let {
                    sendEffect(NavigateToAlbum(it))
                }
            }
            is Event.OnArtistCardClicked -> {
                sendEffect(NavigateToArtist(event.id))
            }

            is Event.OnPulledToRefresh -> viewModelScope.launch {
                updateState { it.copy(isSyncing = true) }
                val succeed = refreshDataUseCase()
                if (!succeed) sendEffect(ShowRefreshingErrorToast)
                updateState { it.copy(isSyncing = false) }

            }

            is Event.OnLikeClicked -> viewModelScope.launch {
                toggleFavoritePhotocardUseCase(event.id)
            }

            is Event.OnDeleteClicked -> {
                updateState { it.copy(dialogState = PhotocardProfileDialogState.ConfirmDeletion) }
            }

            is Event.OnDeletionConfirmed -> viewModelScope.launch {
                deletePhotocardUseCase(photocardId)
                sendEffect(NavigateBack)
            }

            is Event.OnDismissDialogClicked -> {
                updateState { it.copy(dialogState = PhotocardProfileDialogState.None) }
            }
        }
    }

    private fun observeProfile() = collectFlowIntoState(
        flow = getPhotocardProfileDataUseCase(photocardId),
        onSuccess = { state, data -> state.copy(isLoading = false, profile = data) },
        onLoading = { state -> state.copy(isLoading = true) },
        onError = { state, e -> state.copy(isLoading = false, error = e) }
    )
}