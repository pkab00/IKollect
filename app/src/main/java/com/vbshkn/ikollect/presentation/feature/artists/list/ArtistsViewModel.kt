package com.vbshkn.ikollect.presentation.feature.artists.list

import com.vbshkn.ikollect.presentation.feature.artists.list.ArtistsContract.Event
import com.vbshkn.ikollect.presentation.feature.artists.list.ArtistsContract.Effect
import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.RefreshDataUseCase
import com.vbshkn.ikollect.domain.usecase.get.GetArtistListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    private val getArtistListUseCase: GetArtistListUseCase,
    private val refreshDataUseCase: RefreshDataUseCase
) : BaseViewModel<ArtistsUIState, Event, Effect>(initialState = ArtistsUIState()) {
    init {
        collectArtistOverviews()
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.OnPulledToRefresh -> viewModelScope.launch {
                updateState { it.copy(isSyncing = true) }
                val succeed = refreshDataUseCase()
                if (!succeed) sendEffect(Effect.ShowRefreshingErrorToast)
                updateState { it.copy(isSyncing = false) }
            }
        }
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