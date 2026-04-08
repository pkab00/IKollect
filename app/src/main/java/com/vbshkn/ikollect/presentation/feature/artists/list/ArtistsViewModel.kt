package com.vbshkn.ikollect.presentation.feature.artists.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.GetArtistListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    private val getArtistListUseCase: GetArtistListUseCase
) : BaseViewModel<
        ArtistsUIState,
        ArtistsContract.Event,
        ArtistsContract.Effect
        >(initialState = ArtistsUIState()) {
    init {
        collectArtistOverviews()
    }

    override fun onEvent(event: ArtistsContract.Event) {
        // TODO
    }

    private fun collectArtistOverviews() = viewModelScope.launch {
        getArtistListUseCase().collect { networkResult ->
            when(networkResult) {
                is NetworkResult.Loading -> updateState {
                    it.copy(isLoading = true)
                }
                is NetworkResult.Error -> updateState {
                    it.copy(
                        isLoading = false,
                        error = networkResult.error
                    )
                }
                is NetworkResult.Success -> updateState { state ->
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