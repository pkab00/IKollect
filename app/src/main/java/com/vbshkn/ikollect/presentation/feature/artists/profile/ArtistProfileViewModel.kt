package com.vbshkn.ikollect.presentation.feature.artists.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.GetArtistProfileDataUseCase
import com.vbshkn.ikollect.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getArtistProfileDataUseCase: GetArtistProfileDataUseCase
) : BaseViewModel<
        ArtistProfileUIState,
        ArtistProfileContract.Event,
        ArtistProfileContract.Effect
        >(initialState = ArtistProfileUIState()) {
    private val args = savedStateHandle.toRoute<Route.ArtistProfile>()
    private val artistId = args.id

    init {
        observeProfileData()
    }

    override fun onEvent(event: ArtistProfileContract.Event) {
        // TODO
    }

    private fun observeProfileData() = collectFlowIntoState(
        flow = getArtistProfileDataUseCase(artistId),
        onSuccess = { state, data -> state.copy(isLoading = false, profileData = data) },
        onLoading = { state -> state.copy(isLoading = true) },
        onError = { state, e -> state.copy(isLoading = false, error = e) }
    )
}