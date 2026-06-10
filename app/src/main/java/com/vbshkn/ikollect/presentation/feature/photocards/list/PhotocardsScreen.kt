package com.vbshkn.ikollect.presentation.feature.photocards.list

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.vbshkn.ikollect.presentation.feature.photocards.list.PhotocardsContract.Event
import com.vbshkn.ikollect.presentation.feature.photocards.list.PhotocardsContract.Effect
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.TagItem
import com.vbshkn.ikollect.presentation.composable.CommonTopBar
import com.vbshkn.ikollect.presentation.composable.PhotocardItem
import com.vbshkn.ikollect.presentation.composable.PullToRefreshContainer
import com.vbshkn.ikollect.presentation.composable.TagLabel
import com.vbshkn.ikollect.presentation.composable.grid.PhotocardsGrid
import com.vbshkn.ikollect.util.UiText

@Composable
fun PhotocardsScreen(
    viewModel: PhotocardsViewModel,
    onNavigateToWizard: () -> Unit,
    onNavigateToPhotocard: (Long) -> Unit,
    onNavigateToSearch: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is Effect.GoToWizard -> onNavigateToWizard()
                is Effect.GoToPhotocard -> onNavigateToPhotocard(effect.id)
                is Effect.ShowRefreshingErrorToast -> {
                    Toast.makeText(context, R.string.message_unable_to_refresh, Toast.LENGTH_SHORT).show()
                }

                is Effect.GoToSearch -> onNavigateToSearch()
            }
        }
    }

    PullToRefreshContainer(
        isRefreshing = uiState.isSyncing,
        onRefresh = { viewModel.onEvent(Event.OnPulledToRefresh) }
    ) {
        Scaffold(
            topBar = {
                CommonTopBar(
                    title = UiText.StringResource(R.string.screen_title_photocards),
                    counter = uiState.photocards.size,
                    actions = {
                        IconButton({ viewModel.onEvent(Event.OnWizardClicked) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_add),
                                contentDescription = ""
                            )
                        }
                        IconButton({ viewModel.onEvent(Event.OnSearchClicked) }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null
                            )
                        }
                    }
                )
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) { paddingValues ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (uiState.error != null) {
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Text(
                                text = stringResource(R.string.error_loading_photocards),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        TagsRow(
                            items = uiState.tags,
                            selectedTag = uiState.selectedTag,
                            onTagSelected = { viewModel.onEvent(Event.OnTagSelected(it)) }
                        )
                        if (uiState.photocards.isEmpty()) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = stringResource(R.string.filler_nothing_to_show),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            PhotocardsGrid(
                                items = uiState.photocards,
                                onClick = { viewModel.onEvent(Event.OnPhotocardClicked(it.photocardId)) },
                                onHold = { viewModel.onEvent(Event.OnPhotocardPreviewPressed(it.imageUrl)) }
                            )
                        }

                    }
                }
            }
        }
        AnimatedVisibility(
            visible = uiState.fullScreenPreview != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ImageZoomOverlay(
                contentUrl = uiState.fullScreenPreview,
                onDismiss = { viewModel.onEvent(Event.OnPhotocardPreviewReleased) }
            )
        }
    }
}

@Composable
private fun TagsRow(
    items: List<TagItem>,
    selectedTag: TagItem?,
    onTagSelected: (TagItem) -> Unit
) {
    LazyRow (
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(
            items = items,
            key = { it.id }
        ) { item ->
            AnimatedVisibility(
                visible = item == selectedTag || selectedTag == null,
                modifier = Modifier.animateItem()
            ) {
                Box(
                    modifier = Modifier.padding(end = if (item == items.last()) 0.dp else 8.dp)
                ) {
                    TagLabel(
                        tag = item,
                        isSelected = item == selectedTag,
                        onClick = { onTagSelected(item) }
                    )
                }
            }
        }
    }
}