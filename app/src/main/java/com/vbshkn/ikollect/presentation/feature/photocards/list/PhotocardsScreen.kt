package com.vbshkn.ikollect.presentation.feature.photocards.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.composable.PhotocardItem

@Composable
fun PhotocardsScreen(
    viewModel: PhotocardsViewModel,
    onNavigateToWizard: () -> Unit,
    onNavigateToPhotocard: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PhotocardsContract.Effect.GoToWizard -> onNavigateToWizard()
                is PhotocardsContract.Effect.GoToPhotocard -> onNavigateToPhotocard(effect.id)
            }
        }
    }

    Scaffold(
        topBar = {
            TopBar(onEvent = viewModel::onEvent)
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
                .padding(10.dp)
        ) {
            if (uiState.error != null) {
                Text(
                    text = stringResource(R.string.error_loading_photocards),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
            else if (uiState.photocards.isEmpty()) {
                Text(
                    text = stringResource(R.string.filler_nothing_to_show),
                    style = MaterialTheme.typography.labelLarge
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(100.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = uiState.photocards,
                        key = { it.photocardId }
                    ) { photocard ->
                        PhotocardItem(
                            item = photocard,
                            height = 150.dp,
                            onClick = { viewModel.onEvent(PhotocardsContract.Event.OnPhotocardClicked(photocard.photocardId)) },
                            onHold = { viewModel.onEvent(PhotocardsContract.Event.OnPhotocardPreviewPressed(photocard.imageUrl)) }
                        )
                    }
                }
            }
        }
    }
    if (uiState.fullScreenPreview != null) {
        ImageZoomOverlay(
            contentUrl = uiState.fullScreenPreview,
            onDismiss = { viewModel.onEvent(PhotocardsContract.Event.OnPhotocardPreviewReleased) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onEvent: (PhotocardsContract.Event) -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.screen_title_photocards),
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(
                onClick = { onEvent(PhotocardsContract.Event.OnWizardClicked) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = ""
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}