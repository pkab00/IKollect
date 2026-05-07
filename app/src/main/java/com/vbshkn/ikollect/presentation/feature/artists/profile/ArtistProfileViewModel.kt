package com.vbshkn.ikollect.presentation.feature.artists.profile

import com.vbshkn.ikollect.presentation.feature.artists.profile.ArtistProfileContract.Event
import com.vbshkn.ikollect.presentation.feature.artists.profile.ArtistProfileContract.Effect
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.RefreshDataUseCase
import com.vbshkn.ikollect.domain.usecase.get.GetArtistProfileDataUseCase
import com.vbshkn.ikollect.presentation.feature.artists.profile.ArtistProfileContract.Effect.*
import com.vbshkn.ikollect.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getArtistProfileDataUseCase: GetArtistProfileDataUseCase,
    private val refreshDataUseCase: RefreshDataUseCase
) : BaseViewModel<ArtistProfileUIState, Event, Effect>(initialState = ArtistProfileUIState()) {
    private val args = savedStateHandle.toRoute<Route.ArtistProfile>()
    private val artistId = args.id

    init {
        observeProfileData()
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.OnBackClicked -> {
                sendEffect(NavigateBack)
            }
            is Event.OnAlbumCardClicked -> {
                sendEffect(NavigateToAlbum(event.id))
            }
            is Event.OnArtistCardClicked -> {
                sendEffect(NavigateToArtist(event.id))
            }
            is Event.OnPhotocardCardClicked -> {
                sendEffect(NavigateToPhotocard(event.id))
            }
            is Event.OnPulledToRefresh -> viewModelScope.launch {
                updateState { it.copy(isSyncing = true) }
                val succeed = refreshDataUseCase()
                if (!succeed) sendEffect(ShowRefreshingErrorToast)
                updateState { it.copy(isSyncing = false) }
            }
        }
    }

    private fun observeProfileData() = collectFlowIntoState(
        flow = getArtistProfileDataUseCase(artistId),
        onSuccess = { state, data -> state.copy(isLoading = false, profileData = data) },
        onLoading = { state -> state.copy(isLoading = true) },
        onError = { state, e -> state.copy(isLoading = false, error = e) }
    )
}