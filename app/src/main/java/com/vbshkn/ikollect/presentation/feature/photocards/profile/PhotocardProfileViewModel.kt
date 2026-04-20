package com.vbshkn.ikollect.presentation.feature.photocards.profile

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.GetPhotocardProfileDataUseCase
import com.vbshkn.ikollect.presentation.feature.photocards.profile.PhotocardProfileContract.Effect.*
import com.vbshkn.ikollect.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhotocardProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPhotocardProfileDataUseCase: GetPhotocardProfileDataUseCase
) : BaseViewModel<
        PhotocardProfileUIState,
        PhotocardProfileContract.Event,
        PhotocardProfileContract.Effect
        >(initialState = PhotocardProfileUIState()) {
    private val args = savedStateHandle.toRoute<Route.PhotocardFlow.Profile>()
    private val photocardId = args.id

    init {
        observeProfile()
    }

    override fun onEvent(event: PhotocardProfileContract.Event) {
        when (event) {
            is PhotocardProfileContract.Event.OnBackClicked -> {
                sendEffect(NavigateBack)
            }
            is PhotocardProfileContract.Event.OnEditClicked -> {
                sendEffect(NavigateToEdit(photocardId))
            }
            is PhotocardProfileContract.Event.OnOwnerCardClicked -> {
                uiState.value.profile?.photocard?.owner?.artistId?.let {
                    sendEffect(NavigateToArtist(it))
                }
            }
            is PhotocardProfileContract.Event.OnAlbumCardClicked -> {
                uiState.value.profile?.album?.albumId?.let {
                    sendEffect(NavigateToAlbum(it))
                }
            }
            is PhotocardProfileContract.Event.OnArtistCardClicked -> {
                sendEffect(NavigateToArtist(event.id))
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