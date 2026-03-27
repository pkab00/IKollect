package com.vbshkn.ikollect.presentation.feature.artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.usecase.GetArtistOverviewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    private val getArtistOverviewsUseCase: GetArtistOverviewsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ArtistsUIState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            collectArtistOverviews()
        }
    }

    private suspend fun collectArtistOverviews() {
        getArtistOverviewsUseCase().collect { networkResult ->
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
                is NetworkResult.Success -> _uiState.update { state ->
                    val all = networkResult.data
                    val (groups, soloists) = all.partition { it.isGroup }
                    state.copy(
                        isLoading = false,
                        groupOverviews = groups,
                        soloistsOverviews = soloists
                    )
                }
            }
        }
    }
}