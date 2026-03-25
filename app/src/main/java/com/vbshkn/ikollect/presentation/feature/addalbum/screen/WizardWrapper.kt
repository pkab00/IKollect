package com.vbshkn.ikollect.presentation.feature.addalbum.screen

import android.Manifest
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.dialog.ConfirmDialog
import com.vbshkn.ikollect.presentation.dialog.ErrorDialog
import com.vbshkn.ikollect.presentation.dialog.InfoDialog
import com.vbshkn.ikollect.presentation.feature.addalbum.AddAlbumContract
import com.vbshkn.ikollect.presentation.feature.addalbum.AddAlbumDialogState
import com.vbshkn.ikollect.presentation.feature.addalbum.AddAlbumViewModel
import com.vbshkn.ikollect.presentation.navigation.Route

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun WizardWrapper(
    title: String,
    currentRoute: Route.AddAlbumFlow,
    viewModel: AddAlbumViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onExit: () -> Unit,
    onCamera: () -> Unit = {},
    onScanner: () -> Unit = {},
    isLastScreen: Boolean = false,
    content: @Composable ((PaddingValues) -> Unit)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val onEvent: (AddAlbumContract.Event) -> Unit = viewModel::onEvent

    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var pending by rememberSaveable { mutableStateOf(PENDING.NONE) }


    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                viewModel.onEvent(AddAlbumContract.Event.OnExistingPhotoSelected(uri.toString()))
            }
        }
    )

    LaunchedEffect(cameraPermissionState.status) {
        if (cameraPermissionState.status.isGranted) {
            when(pending) {
                PENDING.CAMERA -> {
                    pending = PENDING.NONE
                    onCamera()
                }
                PENDING.SCANNER  -> {
                    pending = PENDING.NONE
                    onScanner()
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when(effect) {
                AddAlbumContract.Effect.Exit -> onExit()
                AddAlbumContract.Effect.NavigateBack -> onBack()
                AddAlbumContract.Effect.NavigateNext -> onNext()
                AddAlbumContract.Effect.OpenGallery -> {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                AddAlbumContract.Effect.TryOpenCamera -> {
                    val status = cameraPermissionState.status
                    when {
                        status.isGranted -> {
                            onCamera()
                        }
                        status.shouldShowRationale -> {
                            pending = PENDING.CAMERA
                            onEvent(AddAlbumContract.Event.OnShowCameraRationale)
                        }
                        else -> {
                            pending = PENDING.CAMERA
                            cameraPermissionState.launchPermissionRequest()
                        }
                    }
                }
                AddAlbumContract.Effect.TryOpenScanner -> {
                    val status = cameraPermissionState.status
                    when {
                        status.isGranted -> {
                            onScanner()
                        }
                        status.shouldShowRationale -> {
                            pending = PENDING.SCANNER
                            onEvent(AddAlbumContract.Event.OnShowCameraRationale)
                        }
                        else -> {
                            pending = PENDING.SCANNER
                            cameraPermissionState.launchPermissionRequest()
                        }
                    }
                }
            }
        }
    }
    DialogHost(
        dialogState = uiState.dialogState,
        onEvent = viewModel::onEvent,
        onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
    )
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(
                        onClick = { onEvent(AddAlbumContract.Event.OnExitClicked) }
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onBack,
                        enabled = viewModel.canNavigateBack(currentRoute)
                    ) { Text(text = stringResource(R.string.wizard_action_back)) }
                    Button(
                        onClick = onNext,
                        enabled = viewModel.canNavigateNext(currentRoute)
                    ) { Text(
                        stringResource(
                            if (isLastScreen) R.string.wizard_action_finish
                            else R.string.wizard_action_next)
                    ) }
                }
            }
        },
        content = { paddingValues ->
            content(paddingValues)
        }
    )
}

@Composable
private fun DialogHost(
    dialogState: AddAlbumDialogState,
    onEvent: (AddAlbumContract.Event) -> Unit,
    onRequestPermission: () -> Unit
) {
    when(dialogState) {
        is AddAlbumDialogState.ConfirmExitDialog -> {
            ConfirmDialog(
                onConfirm = { onEvent(AddAlbumContract.Event.OnExitConfirmed) },
                onDismiss = { onEvent(AddAlbumContract.Event.OnDismissDialog) },
                title = stringResource(R.string.dialog_title_exit),
                text = stringResource(R.string.dialog_body_unsaved_data),
                action = stringResource(R.string.dialog_action_yes)
            )
        }
        is AddAlbumDialogState.CameraRationaleDialog -> {
            InfoDialog(
                title = stringResource(R.string.dialog_title_request_camera),
                text = stringResource(R.string.dialog_body_request_camera),
                onDismiss = {
                    onEvent(AddAlbumContract.Event.OnDismissDialog)
                    onRequestPermission()
                }
            )
        }
        is AddAlbumDialogState.CameraErrorDialog -> {
            ErrorDialog(
                title = stringResource(R.string.dialog_title_failed_saving_photo),
                errorMessage = stringResource(R.string.dialog_body_failed_saving_photo),
                onDismiss = { onEvent(AddAlbumContract.Event.OnDismissDialog) }
            )
        }
        is AddAlbumDialogState.AboutKomcaDialog -> {
            InfoDialog(
                title = stringResource(R.string.dialog_about_komca_title),
                text = stringResource(R.string.dialog_about_komca_body),
                onDismiss = { onEvent(AddAlbumContract.Event.OnDismissDialog) }
            )
        }
        is AddAlbumDialogState.None -> {}
    }
}

private enum class PENDING {
    CAMERA, SCANNER, NONE
}