package com.vbshkn.ikollect.presentation.feature.settings.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.data.local.datastore.LocalTheme
import com.vbshkn.ikollect.data.mapper.toDomain
import com.vbshkn.ikollect.data.mapper.toUiText
import com.vbshkn.ikollect.presentation.feature.settings.SettingsContract
import com.vbshkn.ikollect.presentation.feature.settings.SettingsTopBar
import com.vbshkn.ikollect.presentation.feature.settings.SettingsViewModel
import com.vbshkn.ikollect.presentation.feature.settings.composable.SelectableItem

@Composable
fun ThemeSettingsScreen(
    viewModel: ThemeSettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = state.settings
    val themes = remember { LocalTheme.entries.toList() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ThemeSettingsContract.Effect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            SettingsTopBar(
                onNavigateBack = { viewModel.onEvent(ThemeSettingsContract.Event.OnNavigateBackClicked) },
                title = stringResource(R.string.settings_item_theme)
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(
                items = themes,
                key = { it.ordinal }
            ) { theme ->
                SelectableItem(
                    text = theme.toUiText().asString(),
                    isSelected = settings?.theme == theme.toDomain(),
                    onSelected = { viewModel.onEvent(ThemeSettingsContract.Event.OnNewThemeSelected(theme)) }
                )
            }
        }
    }
}