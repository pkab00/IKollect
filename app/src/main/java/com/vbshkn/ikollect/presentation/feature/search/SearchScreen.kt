package com.vbshkn.ikollect.presentation.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.presentation.composable.LoadingOverlay

@Composable
fun SearchScreen(
    viewModel: AbstractSearchViewModel<*>,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    content: @Composable () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SearchContract.Effect.NavigateBack -> onNavigateBack()
                is SearchContract.Effect.NavigateToDetail -> onNavigateToDetail(effect.id)
            }
        }
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                query = uiState.query,
                onQueryChange = { viewModel.onEvent(SearchContract.Event.OnQueryChange(it)) },
                onClearQuery = { viewModel.onEvent(SearchContract.Event.OnClearQuery) },
                onNavigateBack = { viewModel.onEvent(SearchContract.Event.OnNavigateBack) }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            content()
        }
    }
    if (uiState.isLoading) LoadingOverlay()
}

