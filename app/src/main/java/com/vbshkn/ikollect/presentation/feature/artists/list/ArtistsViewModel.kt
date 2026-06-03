package com.vbshkn.ikollect.presentation.feature.artists.list

import com.vbshkn.ikollect.presentation.feature.artists.list.ArtistsContract.Event
import com.vbshkn.ikollect.presentation.feature.artists.list.ArtistsContract.Effect
import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.business.ArtistFilter
import com.vbshkn.ikollect.domain.usecase.RefreshDataUseCase
import com.vbshkn.ikollect.domain.usecase.get.GetAllArtistsUseCase
import com.vbshkn.ikollect.presentation.feature.artists.list.ArtistsContract.Effect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    private val getAllArtistsUseCase: GetAllArtistsUseCase,
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
                if (!succeed) sendEffect(ShowRefreshingErrorToast)
                updateState { it.copy(isSyncing = false) }
            }

            is Event.OnArtistClick -> {
                sendEffect(NavigateToArtist(event.artistId))
            }

            is Event.OnSelectFilter -> {
                if (uiState.value.artistFilter != event.filter) {
                    updateState { it.copy(artistFilter = event.filter) }
                }
                else {
                    updateState { it.copy(artistFilter = ArtistFilter.ALL) }
                }
            }

            Event.OnSearchClick -> {
                sendEffect(NavigateToSearch)
            }
        }
    }

    private fun collectArtistOverviews() = viewModelScope.launch {
        uiState
            .distinctUntilChanged { old, new -> old.artistFilter == new.artistFilter }
            .combine(getAllArtistsUseCase()) { state, result ->
            when (result) {
                is NetworkResult.Error -> updateState {
                    it.copy(error = result.error, isLoading = false)
                }
                is NetworkResult.Loading -> updateState {
                    it.copy(isLoading = true)
                }
                is NetworkResult.Success -> {
                    when (state.artistFilter) {
                        ArtistFilter.GROUPS -> updateState { state ->
                            state.copy(artists = result.data.filter { it.isGroup }, isLoading = false)
                        }
                        ArtistFilter.SOLOISTS -> updateState { state ->
                            state.copy(artists = result.data.filter { !it.isGroup }, isLoading = false)
                        }
                        ArtistFilter.ALL -> updateState { state ->
                            state.copy(artists = result.data, isLoading = false)
                        }
                    }
                }
            }
        }.collect()
    }
}