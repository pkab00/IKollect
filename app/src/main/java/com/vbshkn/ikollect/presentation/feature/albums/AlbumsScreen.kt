package com.vbshkn.ikollect.presentation.feature.albums

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.data.AppError
import com.vbshkn.ikollect.domain.model.Album
import com.vbshkn.ikollect.presentation.composable.LoadingOverlay
import com.vbshkn.ikollect.presentation.dialog.ConfirmDialog
import com.vbshkn.ikollect.presentation.dialog.ErrorDialog
import com.vbshkn.ikollect.presentation.dialog.InfoDialog

@Composable
fun AlbumsScreen(
    viewModel: AlbumsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val onEvent: (AlbumsContract.Event) -> Unit = viewModel::onEvent

    DialogHost(
        dialogState = uiState.dialogState,
        onEvent = onEvent
    )

    Scaffold(
        topBar = { TopBar(onEvent) },
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
                ErrorScreen()
            }
            else if (uiState.albums.isEmpty()) {
                NoAlbumsScreen()
            }
            else {
                AlbumsGrid(uiState.albums)
            }
        }

        if (uiState.isLoading) {
            LoadingOverlay()
        }
    }
}

@Composable
fun AlbumsGrid(albums: List<Album>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = albums,
            key = { it.albumId }
        ) { album ->
            AlbumItem("${album.artists.joinToString { it.name }} - ${album.name}")
        }
    }
}

@Composable
fun NoAlbumsScreen() {
    Text(
        text = stringResource(R.string.message_no_albums_found),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
    )
}

@Composable
fun ErrorScreen() {
    Text(
        text = stringResource(R.string.error_loading_albums),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onEvent: (AlbumsContract.Event) -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.screen_title_albums),
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(
                onClick = { onEvent(AlbumsContract.Event.OnStartScanningClicked) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_scanner),
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

@Composable
private fun AlbumItem(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.wrapContentSize()
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DialogHost(
    dialogState: AlbumsDialogState,
    onEvent: (AlbumsContract.Event) -> Unit
) {
    when(dialogState) {
        is AlbumsDialogState.ScanningErrorDialog -> {
            ErrorDialog(
                title = stringResource(R.string.error_title_scanning),
                errorMessage = dialogState.errorMessage.asString(),
                onDismiss = { onEvent(AlbumsContract.Event.OnDismissDialogClicked) },
            )
        }
        is AlbumsDialogState.ScanningResultDialog -> {
            ConfirmDialog(
                title = stringResource(R.string.dialog_title_album_detected),
                action = stringResource(R.string.dialog_action_next),
                text = dialogState.message,
                onConfirm = { onEvent(AlbumsContract.Event.OnAlbumSavingConfirmed) },
                onDismiss = { onEvent(AlbumsContract.Event.OnDismissDialogClicked) },
            )
        }
        is AlbumsDialogState.None -> {}
    }
}