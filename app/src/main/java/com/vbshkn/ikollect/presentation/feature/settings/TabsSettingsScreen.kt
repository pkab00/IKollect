package com.vbshkn.ikollect.presentation.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.navigation.NavBarDestinations
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun TabsSettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = state.settings

    val initialTabs = remember(settings?.navBarDestinations) {
        settings?.navBarDestinations?.filter { it != NavBarDestinations.PROFILE } ?: emptyList()
    }
    var tabs by remember(initialTabs) { mutableStateOf(initialTabs) }

    val hapticFeedback = LocalHapticFeedback.current
    val lazyListState = rememberLazyListState()
    val reorderableLazyCollectionState = rememberReorderableLazyListState(lazyListState) { from, to ->
        tabs = tabs.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }
    val isDragging = reorderableLazyCollectionState.isAnyItemDragging

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SettingsContract.Effect.GoBack -> onNavigateBack()
                else -> {}
            }
        }
    }

    LaunchedEffect(isDragging) {
        if (tabs != initialTabs) {
            viewModel.onEvent(SettingsContract.Event.OnTabsReordered(tabs + NavBarDestinations.PROFILE))
        }
    }

    Scaffold(
        topBar = {
            SettingsTopBar(
                onEvent = viewModel::onEvent,
                title = stringResource(R.string.settings_item_tabs_order)
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { paddingValues ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(tabs, { it.name }) { tab ->
                ReorderableItem(reorderableLazyCollectionState, tab.name) {
                    DraggableItem(
                        text = stringResource(tab.labelRes),
                        modifier = Modifier
                            .draggableHandle(
                            onDragStarted = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                            },
                            onDragStopped = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                            }
                        )
                    )
                }
            }
        }
    }
}