package com.vbshkn.ikollect.presentation.feature.photocards.wizard.steps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.TagItem
import com.vbshkn.ikollect.presentation.composable.TagLabel
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardContract
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardUIState
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardViewModel
import com.vbshkn.ikollect.presentation.feature.wizard.WizardItemWrapper
import com.vbshkn.ikollect.util.UiText
import kotlinx.coroutines.launch

@Composable
fun WrapUpScreen(viewModel: PhotocardWizardViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            WizardItemWrapper(
                title = UiText.StringResource(R.string.photocard_wizard_subtitle_display_name),
                content = { PhotocardDisplayName(uiState, viewModel::onEvent) }
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            WizardItemWrapper(
                title = UiText.StringResource(R.string.photocard_wizard_subtitle_tags),
                content = {
                    PhotocardTags(
                        selectedTags = uiState.tags.filter { it.id in uiState.photocardCandidate.tagIds },
                        onEvent = viewModel::onEvent
                    )
                }
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            WizardItemWrapper(
                title = UiText.StringResource(R.string.photocard_wizard_subtitle_notes),
                content = { UserNotesField(
                    value = uiState.photocardCandidate.userNote,
                    onValueChange = {
                        viewModel.onEvent(PhotocardWizardContract.Event.OnUserNotesChanged(it))
                    }
                ) }
            )
        }
    }
    TagSelectionSheet(
        allTags = uiState.tags,
        selectedTagIds = uiState.photocardCandidate.tagIds,
        enabled = uiState.enableTagSelector,
        onTagClick = { viewModel.onEvent(PhotocardWizardContract.Event.OnTagSelected(it)) },
        onDismiss = { viewModel.onEvent(PhotocardWizardContract.Event.OnDismissTagSelector) }
    )
}

@Composable
private fun PhotocardDisplayName(
    uiState: PhotocardWizardUIState,
    onEvent: (PhotocardWizardContract.Event) -> Unit
) {
    val isError = uiState.photocardCandidate.displayName.length > 100

    OutlinedTextField(
        value = uiState.photocardCandidate.displayName,
        onValueChange = { newValue ->
            onEvent(PhotocardWizardContract.Event.OnDisplayedNameChanged(newValue))
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues()),

        supportingText = {
            if (isError) {
                Text(
                    text = stringResource(R.string.textfield_text_too_long),
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(stringResource(R.string.photocard_name_field_suppoting_text))
            }
        },
        isError = isError,
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun PhotocardTags(
    selectedTags: List<TagItem>,
    onEvent: (PhotocardWizardContract.Event) -> Unit
) {
    Surface(
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        LazyVerticalGrid(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            columns = GridCells.Adaptive(70.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            items(
                items = selectedTags,
                key = { it.id }
            ) { tag ->
                TagLabel(
                    tag = tag,
                    isSelected = false,
                    onClick = { onEvent(PhotocardWizardContract.Event.OnTagSelected(tag.id)) },
                    modifier = Modifier
                        .size(30.dp)
                        .animateItem()
                )
            }
            item {
                Surface(
                    border = BorderStroke(0.5f.dp, MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { onEvent(PhotocardWizardContract.Event.OnAddTagClicked) }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
private fun TagSelectionSheet(
    allTags: List<TagItem>,
    selectedTagIds: Set<Long>,
    enabled: Boolean,
    onTagClick: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    if (enabled) {
        ModalBottomSheet(
            onDismissRequest = {
                onDismiss()
                scope.launch { sheetState.hide() }
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.tag_selector_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(100.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allTags) { tag ->
                        TagLabel(
                            tag = tag,
                            isSelected = tag.id in selectedTagIds,
                            onClick = { onTagClick(tag.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserNotesField(
    value: String,
    onValueChange: (String) -> Unit
) {
    val maxChar = 1500

    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length <= maxChar) onValueChange(it)
        },
        placeholder = { Text(stringResource(R.string.photocard_notes_placeholder)) },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        minLines = 8,
        supportingText = {
            Text(
                text = "${value.length} / $maxChar",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodySmall
            )
        },
        isError = value.length >= maxChar,
        shape = RoundedCornerShape(12.dp)
    )
}