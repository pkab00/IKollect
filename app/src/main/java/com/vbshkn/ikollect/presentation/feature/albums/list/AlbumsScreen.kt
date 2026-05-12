package com.vbshkn.ikollect.presentation.feature.albums.list

import android.widget.Toast
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsContract.Event
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsContract.Effect
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.details.AlbumDetails
import com.vbshkn.ikollect.domain.model.candidate.AlbumCandidate
import com.vbshkn.ikollect.presentation.composable.AlbumCard
import com.vbshkn.ikollect.presentation.composable.CommonTopBar
import com.vbshkn.ikollect.presentation.composable.LoadingOverlay
import com.vbshkn.ikollect.presentation.composable.PullToRefreshContainer
import com.vbshkn.ikollect.presentation.composable.SmallTextLabel
import com.vbshkn.ikollect.presentation.composable.dialog.ConfirmDialog
import com.vbshkn.ikollect.presentation.composable.dialog.ErrorDialog
import com.vbshkn.ikollect.util.UiText

@Composable
fun AlbumsScreen(
    viewModel: AlbumsViewModel,
    onGoToWizard: (AlbumCandidate) -> Unit,
    onAlbumClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val onEvent: (Event) -> Unit = viewModel::onEvent

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is Effect.NavigateToAlbum -> onAlbumClick(effect.id)
                is Effect.NavigateToSaveFlow -> onGoToWizard(effect.candidate)
                is Effect.ShowRefreshingErrorToast -> {
                    Toast.makeText(context, R.string.message_unable_to_refresh, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    DialogHost(
        dialogState = uiState.dialogState,
        onEvent = onEvent
    )

    PullToRefreshContainer(
        isRefreshing = uiState.isSyncing,
        onRefresh = { onEvent(Event.OnPulledToSync) }
    ) {
        Scaffold(
            topBar = {
                CommonTopBar(
                    title = UiText.StringResource(R.string.screen_title_albums),
                    actions = {
                        IconButton({ onEvent(Event.OnStartScanningClicked) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_scanner),
                                contentDescription = ""
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
                    .padding(10.dp)
            ) {
                if (uiState.albums.isEmpty() && uiState.error != null) {
                    ErrorScreen()
                } else if (uiState.albums.isEmpty()) {
                    NoAlbumsScreen()
                } else {
                    AlbumsGrid(uiState.albums, viewModel::onEvent)
                }
            }

            if (uiState.isLoading) {
                LoadingOverlay()
            }
        }
    }
}

@Composable
fun AlbumsGrid(
    albums: List<AlbumDetails>,
    onEvent: (Event) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = albums,
            key = { it.albumId }
        ) { album ->
            AlbumCard(
                album = album,
                onClick = { onEvent(Event.OnAlbumClicked(album.albumId)) }
            )
        }
    }
}

@Composable
fun NoAlbumsScreen() {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = stringResource(R.string.message_no_albums_found),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ErrorScreen() {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = stringResource(R.string.error_loading_albums),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DialogHost(
    dialogState: AlbumsDialogState,
    onEvent: (Event) -> Unit
) {
    when (dialogState) {
        is AlbumsDialogState.ScanningErrorDialog -> {
            ErrorDialog(
                title = stringResource(R.string.error_title_scanning),
                errorMessage = dialogState.errorMessage.asString(),
                onDismiss = { onEvent(Event.OnDismissDialogClicked) },
            )
        }

        is AlbumsDialogState.ScanningResultDialog -> {
            ConfirmDialog(
                title = stringResource(R.string.dialog_title_album_detected),
                action = stringResource(R.string.dialog_action_next),
                text = dialogState.message,
                onConfirm = { onEvent(Event.OnAlbumSavingConfirmed) },
                onDismiss = { onEvent(Event.OnDismissDialogClicked) },
            )
        }

        is AlbumsDialogState.None -> {}
    }
}