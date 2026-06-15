package com.vbshkn.ikollect.presentation.feature.settings.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.composable.TagContainer
import com.vbshkn.ikollect.presentation.composable.profile.WrappingTitle
import com.vbshkn.ikollect.presentation.feature.settings.SettingsTopBar
import com.vbshkn.ikollect.util.UiText

@Composable
fun TagSettingsScreen(
    viewModel: TagSettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                TagSettingsContract.Effect.NavigateBack -> onNavigateBack()
            }
        }
    }

    DialogHost(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )

    Scaffold(
        topBar = {
            SettingsTopBar(
                onNavigateBack = { viewModel.onEvent(TagSettingsContract.Event.OnNavigateBackClicked) },
                title = stringResource(R.string.settings_item_manage_tags)
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
            item {
                WrappingTitle(
                    title = UiText.StringResource(R.string.tag_settings_title_custom),
                    subTitle = UiText.StringResource(R.string.tag_settings_subtitle_custom)
                ) {
                    TagContainer(
                        tags = uiState.customTags,
                        onTagClick = { viewModel.onEvent(TagSettingsContract.Event.OnCustomTagSelected(it)) },
                        onNewTagClick = { viewModel.onEvent(TagSettingsContract.Event.OnNewTagClicked) }
                    )
                }
            }
            item {
                WrappingTitle(
                    title = UiText.StringResource(R.string.tag_settings_title_system)
                ) {
                    TagContainer(
                        tags = uiState.systemTags,
                        enableNewTag = false,
                        onTagClick = {},
                        onNewTagClick = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogHost(
    uiState: TagSettingsUiState,
    onEvent: (TagSettingsContract.Event) -> Unit
) {
    when (uiState.dialogState) {
        is TagSettingsDialogState.CreateTagDialog -> EditTagDialog(
            title = UiText.StringResource(R.string.title_create_tag),
            allTagColors = uiState.customTags.map { it.color },
            onDone = { onEvent(TagSettingsContract.Event.OnSaveNewTagConfirmed(it)) },
            onDismissRequest = { onEvent(TagSettingsContract.Event.OnDismissDialogClicked) }
        )
        is TagSettingsDialogState.EditTagDialog -> EditTagDialog(
            title = UiText.StringResource(R.string.title_edit_tag),
            editedTag = uiState.dialogState.selectedTag,
            allTagColors = uiState.customTags.map { it.color },
            onDone = { onEvent(TagSettingsContract.Event.OnEditTagConfirmed(it)) },
            onDelete = { onEvent(TagSettingsContract.Event.OnDeleteTagConfirmed(it)) },
            onDismissRequest = { onEvent(TagSettingsContract.Event.OnDismissDialogClicked) }
        )
        is TagSettingsDialogState.None -> {}
    }
}