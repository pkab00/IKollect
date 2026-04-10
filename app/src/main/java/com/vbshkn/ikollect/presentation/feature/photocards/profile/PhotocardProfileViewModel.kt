package com.vbshkn.ikollect.presentation.feature.photocards.profile

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.GetPhotocardProfileDataUseCase
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
    private val args = savedStateHandle.toRoute<Route.PhotocardProfile>()
    private val photocardId = args.id

    init {
        observeProfile()
    }

    override fun onEvent(event: PhotocardProfileContract.Event) {
        when (event) {
            is PhotocardProfileContract.Event.OnBackClicked -> {
                sendEffect(PhotocardProfileContract.Effect.NavigateBack)
            }
            is PhotocardProfileContract.Event.OnOwnerCardClicked -> {
                uiState.value.profile?.photocard?.owner?.artistId?.let {
                    sendEffect(PhotocardProfileContract.Effect.NavigateToArtist(it))
                }
            }
            is PhotocardProfileContract.Event.OnAlbumCardClicked -> {
                uiState.value.profile?.album?.albumId?.let {
                    sendEffect(PhotocardProfileContract.Effect.NavigateToAlbum(it))
                }
            }
            is PhotocardProfileContract.Event.OnArtistCardClicked -> {
                sendEffect(PhotocardProfileContract.Effect.NavigateToArtist(event.id))
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