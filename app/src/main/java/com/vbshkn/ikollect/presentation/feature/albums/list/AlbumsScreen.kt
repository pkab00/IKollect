package com.vbshkn.ikollect.presentation.feature.albums.list

import android.Manifest.permission.CAMERA
import android.widget.Toast
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsContract.Event
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsContract.Effect
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.candidate.AlbumCandidate
import com.vbshkn.ikollect.presentation.composable.CommonTopBar
import com.vbshkn.ikollect.presentation.composable.LoadingOverlay
import com.vbshkn.ikollect.presentation.composable.PullToRefreshContainer
import com.vbshkn.ikollect.presentation.composable.dialog.ConfirmDialog
import com.vbshkn.ikollect.presentation.composable.dialog.ErrorDialog
import com.vbshkn.ikollect.presentation.composable.grid.AlbumsGrid
import com.vbshkn.ikollect.presentation.feature.wizard.dialog.CameraRationaleDialog
import com.vbshkn.ikollect.util.UiText

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AlbumsScreen(
    viewModel: AlbumsViewModel,
    onGoToWizard: (AlbumCandidate) -> Unit,
    onGoToSearch: () -> Unit,
    onGoToScanning: () -> Unit,
    onAlbumClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(CAMERA)
    var pending by remember { mutableStateOf(false) }
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
                is Effect.NavigateToSearch -> onGoToSearch()
                is Effect.TryOpenBarcodeScanner -> when (permissionState.status) {
                    is PermissionStatus.Denied -> {
                        if (permissionState.status.shouldShowRationale) {
                            pending = true
                            onEvent(Event.OnDismissDialogClicked)
                            onEvent(Event.OnShowCameraRationale)
                        } else {
                            pending = true
                            permissionState.launchPermissionRequest()
                        }
                    }
                    is PermissionStatus.Granted -> {
                        onGoToScanning()
                    }
                }
            }
        }
    }

    LaunchedEffect(permissionState.status) {
        if (pending && permissionState.status is PermissionStatus.Granted) {
            pending = false
            onGoToScanning()
        }
    }

    DialogHost(
        dialogState = uiState.dialogState,
        onEvent = onEvent,
        onRequestPermission = { permissionState.launchPermissionRequest() }
    )

    PullToRefreshContainer(
        isRefreshing = uiState.isSyncing,
        onRefresh = { onEvent(Event.OnPulledToSync) }
    ) {
        Scaffold(
            topBar = {
                CommonTopBar(
                    title = UiText.StringResource(R.string.screen_title_albums),
                    counter = uiState.albums.size,
                    actions = {
                        IconButton({ onEvent(Event.OnScanningClicked) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_scanner),
                                contentDescription = null
                            )
                        }
                        IconButton({ onEvent(Event.OnSearchClicked) }) {
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
                if (uiState.albums.isEmpty() && uiState.error != null) {
                    ErrorScreen()
                } else if (uiState.albums.isEmpty()) {
                    NoAlbumsScreen()
                } else {
                    AlbumsGrid(
                        items = uiState.albums,
                        onClick = { album -> viewModel.onEvent(Event.OnAlbumClicked(album.albumId)) }
                    )
                }
            }

            if (uiState.isLoading) {
                LoadingOverlay()
            }
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
    onEvent: (Event) -> Unit,
    onRequestPermission: () -> Unit
) {
    when (dialogState) {
        is AlbumsDialogState.CameraRationaleDialog -> {
            CameraRationaleDialog {
                onEvent(Event.OnDismissDialogClicked)
                onRequestPermission()
            }
        }

        is AlbumsDialogState.None -> {}
    }
}