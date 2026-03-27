package com.vbshkn.ikollect.presentation.feature.artists.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vbshkn.ikollect.data.remote.NetworkResult
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
) : ViewModel() {
    private val args = savedStateHandle.toRoute<Route.ArtistProfile>()
    private val artistId = args.id
    private val _uiState = MutableStateFlow(ArtistProfileUIState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            collectProfileData()
        }
    }

    private suspend fun collectProfileData() =
        getArtistProfileDataUseCase(artistId).collect { networkResult ->
            when(networkResult) {
                is NetworkResult.Loading -> _uiState.update {
                    it.copy(isLoading = true)
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = networkResult.error
                    )
                }
                is NetworkResult.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        profileData = networkResult.data
                    )
                }
            }
        }
}