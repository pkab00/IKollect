package com.vbshkn.ikollect.presentation.feature.settings

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
import com.vbshkn.ikollect.data.local.datastore.LocalLanguage
import com.vbshkn.ikollect.data.mapper.toDomain
import com.vbshkn.ikollect.data.mapper.toUiText

@Composable
fun LanguageSettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = state.settings
    val languages = remember { LocalLanguage.entries.toList() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SettingsContract.Effect.GoBack -> onNavigateBack()
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            SettingsTopBar(
                onEvent = viewModel::onEvent,
                title = stringResource(R.string.settings_item_language)
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
                items = languages,
                key = { it.ordinal }
            ) { language ->
                SelectableItem(
                    text = language.toUiText().asString(),
                    isSelected = settings?.language == language.toDomain(),
                    onSelected = { viewModel.onEvent(SettingsContract.Event.OnNewLanguageSelected(language)) }
                )
            }
        }
    }
}