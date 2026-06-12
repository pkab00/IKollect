package com.vbshkn.ikollect.presentation.feature.search

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.presentation.feature.search.SearchContract.Event
import com.vbshkn.ikollect.presentation.feature.search.SearchContract.Effect
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.model.Searchable
import com.vbshkn.ikollect.domain.model.getMatching
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

abstract class AbstractSearchViewModel<T : Searchable>
    : BaseViewModel<SearchUiState<T>, Event, Effect>(initialState = SearchUiState()) {

    private val searchQuery = MutableStateFlow("")

    override fun onEvent(event: Event) {
        when (event) {
            is Event.OnClearQuery -> {
                updateState { it.copy(query = "") }
            }

            is Event.OnNavigateBack -> {
                sendEffect(Effect.NavigateBack)
            }

            is Event.OnNavigateToDetail -> {
                sendEffect(Effect.NavigateToDetail(event.id))
            }

            is Event.OnQueryChange -> viewModelScope.launch {
                updateState { it.copy(query = event.query) }
                searchQuery.update { event.query }
            }
        }
    }

    @OptIn(FlowPreview::class)
    protected fun observeSearchResults(searchResultsFlow: Flow<NetworkResult<List<T>>>) {
        viewModelScope.launch {
            searchQuery
                .debounce(300.milliseconds)
                .combine(searchResultsFlow) { query, result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val filteredResults = if (query.isBlank()) {
                            result.data
                        } else {
                            result.data.getMatching(query)
                        }
                        updateState { it.copy(results = filteredResults, isLoading = false) }
                    }
                    is NetworkResult.Error -> {
                        updateState { it.copy(isLoading = false) }
                        Log.e("SearchViewModel", "Error fetching search results: ${result.error}")
                    }
                    is NetworkResult.Loading -> {
                        updateState { it.copy(isLoading = true) }
                    }
                }
            }.collect()
        }
    }
}