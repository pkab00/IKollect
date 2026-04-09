package com.vbshkn.ikollect.presentation.feature.albums.profile

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.GetAlbumProfileDataUseCase
import com.vbshkn.ikollect.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlbumProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAlbumProfileDataUseCase: GetAlbumProfileDataUseCase
) : BaseViewModel<
        AlbumProfileUIState,
        AlbumProfileContract.Event,
        AlbumProfileContract.Effect
        >(initialState = AlbumProfileUIState()) {
    private val args = savedStateHandle.toRoute<Route.AlbumProfile>()
    private val albumId = args.id

    init {
        observeProfileData()
    }

    override fun onEvent(event: AlbumProfileContract.Event) {
        when (event) {
            is AlbumProfileContract.Event.OnBackClicked -> {
                sendEffect(AlbumProfileContract.Effect.NavigateBack)
            }
            is AlbumProfileContract.Event.OnOwnerClicked -> {
                val ownerId = uiState.value.profile?.album?.artists[0]?.artistId
                ownerId?.let { sendEffect(AlbumProfileContract.Effect.NavigateToArtist(it)) }
            }
            is AlbumProfileContract.Event.OnArtistCardClicked -> {
                sendEffect(AlbumProfileContract.Effect.NavigateToArtist(event.id))
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