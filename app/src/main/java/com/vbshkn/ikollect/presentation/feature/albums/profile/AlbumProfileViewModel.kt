package com.vbshkn.ikollect.presentation.feature.albums.profile

import com.vbshkn.ikollect.presentation.feature.albums.profile.AlbumProfileContract.Effect
import com.vbshkn.ikollect.presentation.feature.albums.profile.AlbumProfileContract.Event
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.RefreshDataUseCase
import com.vbshkn.ikollect.domain.usecase.delete.DeleteAlbumUseCase
import com.vbshkn.ikollect.domain.usecase.favorite.ToggleFavoriteAlbumUseCase
import com.vbshkn.ikollect.domain.usecase.get.GetAlbumProfileDataUseCase
import com.vbshkn.ikollect.presentation.feature.albums.profile.AlbumProfileContract.Effect.*
import com.vbshkn.ikollect.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAlbumProfileDataUseCase: GetAlbumProfileDataUseCase,
    private val refreshDataUseCase: RefreshDataUseCase,
    private val toggleFavoriteAlbumUseCase: ToggleFavoriteAlbumUseCase,
    private val deleteAlbumUseCase: DeleteAlbumUseCase
) : BaseViewModel<AlbumProfileUIState, Event, Effect>(initialState = AlbumProfileUIState()) {
    private val args = savedStateHandle.toRoute<Route.AlbumFlow.Profile>()
    private val albumId = args.id

    init {
        observeProfileData()
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.OnBackClicked -> {
                sendEffect(NavigateBack)
            }
            is Event.OnOwnerClicked -> {
                val ownerId = uiState.value.profile?.album?.artists[0]?.artistId
                ownerId?.let { sendEffect(NavigateToArtist(it)) }
            }
            is Event.OnArtistCardClicked -> {
                sendEffect(NavigateToArtist(event.id))
            }
            is Event.OnPhotocardCardClicked -> {
                sendEffect(NavigateToPhotocard(event.id))
            }
            is Event.OnEditClicked -> {
                sendEffect(NavigateToEdit)
            }
            is Event.OnPulledToRefresh -> viewModelScope.launch {
                updateState { it.copy(isSyncing = true) }
                val succeed = refreshDataUseCase()
                if (!succeed) sendEffect(ShowRefreshingErrorToast)
                updateState { it.copy(isSyncing = false) }
            }
            is Event.OnLikeClicked -> viewModelScope.launch {
                toggleFavoriteAlbumUseCase(event.id)
            }
            is Event.OnDeletionConfirmed -> viewModelScope.launch {
                deleteAlbumUseCase(albumId)
                sendEffect(NavigateBack)
            }
            is Event.OnDismissDialogClicked -> {
                updateState { it.copy(dialogState = AlbumProfileDialogState.None) }
            }
            is Event.OnDeleteClicked -> {
                updateState { it.copy(dialogState = AlbumProfileDialogState.ConfirmDeletion) }
            }
        }
    }

    private fun observeProfileData() = collectFlowIntoState(
        flow = getAlbumProfileDataUseCase(albumId),
        onSuccess = { state, data ->  state.copy(profile = data, isLoading = false) },
        onLoading = { state -> state.copy(isLoading = true) },
        onError = { state, e -> state.copy(isLoading = false, error = e) }
    )
}