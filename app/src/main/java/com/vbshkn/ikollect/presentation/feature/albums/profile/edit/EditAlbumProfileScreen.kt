package com.vbshkn.ikollect.presentation.feature.albums.profile.edit

import android.Manifest
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.composable.ImageChangerItem
import com.vbshkn.ikollect.presentation.composable.PlainTextField
import com.vbshkn.ikollect.presentation.feature.wizard.WizardItemWrapper
import com.vbshkn.ikollect.presentation.feature.wizard.dialog.CameraRationaleDialog
import com.vbshkn.ikollect.util.UiText
import androidx.lifecycle.SavedStateHandle
import com.vbshkn.ikollect.domain.model.UserItemImage
import com.vbshkn.ikollect.presentation.composable.CameraResultObserver
import com.vbshkn.ikollect.presentation.composable.ScannerResultObserver
import com.vbshkn.ikollect.presentation.feature.camera.CameraResultContract

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EditAlbumProfileScreen(
    viewModel: EditAlbumProfileViewModel,
    onNavigateBack: () -> Unit,
    onOpenCamera: () -> Unit,
    onOpenScanner: () -> Unit = {},
    savedStateHandle: SavedStateHandle
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var pending by rememberSaveable { mutableStateOf(Pending.NONE) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                } catch (e: SecurityException) { }
                viewModel.onEvent(
                    EditAlbumProfileContract.Event.OnImageChanged(
                        UserItemImage(
                            uri = uri.toString(),
                            isCached = false
                        )
                    )
                )
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is EditAlbumProfileContract.Effect.NavigateBack -> onNavigateBack()
                is EditAlbumProfileContract.Effect.OpenGallery -> galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )

                is EditAlbumProfileContract.Effect.TryOpenScanner -> {
                    val status = cameraPermissionState.status
                    when {
                        status.isGranted -> {
                            onOpenScanner()
                        }

                        status.shouldShowRationale -> {
                            viewModel.onEvent(EditAlbumProfileContract.Event.OnShowCameraRationale)
                            pending = Pending.SCANNER
                        }

                        else -> {
                            cameraPermissionState.launchPermissionRequest()
                            pending = Pending.SCANNER
                        }
                    }
                }

                is EditAlbumProfileContract.Effect.TryOpenCamera -> {
                    val status = cameraPermissionState.status
                    when {
                        status.isGranted -> {
                            onOpenCamera()
                        }

                        status.shouldShowRationale -> {
                            viewModel.onEvent(EditAlbumProfileContract.Event.OnShowCameraRationale)
                            pending = Pending.CAMERA
                        }

                        else -> {
                            cameraPermissionState.launchPermissionRequest()
                            pending = Pending.CAMERA
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(cameraPermissionState.status) {
        if (cameraPermissionState.status.isGranted) {
            when (pending) {
                Pending.CAMERA -> onOpenCamera()
                Pending.SCANNER -> onOpenScanner()
                Pending.NONE -> {}
            }
            pending = Pending.NONE
        }
    }

    CameraResultObserver(
        savedStateHandle = savedStateHandle,
        onResult = { result ->
            viewModel.onEvent(EditAlbumProfileContract.Event.OnImageChanged(result))
        }
    )

    DialogHost(
        dialogState = uiState.dialogState,
        onEvent = viewModel::onEvent,
        onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
    )

    ScannerResultObserver(
        savedStateHandle = savedStateHandle,
        onResult = { result ->
            viewModel.onEvent(EditAlbumProfileContract.Event.OnKomcaNumberChanged(result))
        }
    )

    Scaffold(
        topBar = { TopBar(viewModel::onEvent) }
    ) { paddingValues ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 8.dp)
        ) {
            item {
                ImageChangerItem(
                    imageUrl = uiState.image?.uri,
                    onOpenCamera = { viewModel.onEvent(EditAlbumProfileContract.Event.OnOpenCameraClicked) },
                    onOpenGallery = { viewModel.onEvent(EditAlbumProfileContract.Event.OnOpenGalleryClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            item {
                WizardItemWrapper(UiText.StringResource(R.string.album_profile_label_title)) {
                    AlbumNameField(value = uiState.albumName, onEvent = viewModel::onEvent)
                    AlbumVersionField(value = uiState.albumVersion, onEvent = viewModel::onEvent)
                }
            }

            item {
                WizardItemWrapper(UiText.StringResource(R.string.album_profile_label_komca)) {
                    KomcaNumberField(value = uiState.komcaNumber, onEvent = viewModel::onEvent)
                }
            }

            item {
                WizardItemWrapper(UiText.StringResource(R.string.profile_title_notes)) {
                    UserNotesField(value = uiState.userNotes, onEvent = viewModel::onEvent)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onEvent: (EditAlbumProfileContract.Event) -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.title_edit_album),
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = { onEvent(EditAlbumProfileContract.Event.OnBackClicked) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(onClick = { onEvent(EditAlbumProfileContract.Event.OnSaveChangesClicked) }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
fun AlbumNameField(
    value: String,
    onEvent: (EditAlbumProfileContract.Event) -> Unit
) {
    PlainTextField(
        value = value,
        onValueChange = { onEvent(EditAlbumProfileContract.Event.OnAlbumNameChanged(it)) },
        title = UiText.StringResource(R.string.album_profile_label_title),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
    )
}

@Composable
fun AlbumVersionField(
    value: String,
    onEvent: (EditAlbumProfileContract.Event) -> Unit
) {
    PlainTextField(
        value = value,
        onValueChange = { onEvent(EditAlbumProfileContract.Event.OnAlbumVersionChanged(it)) },
        title = UiText.StringResource(R.string.album_profile_label_version),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
    )
}

@Composable
fun KomcaNumberField(
    value: String,
    onEvent: (EditAlbumProfileContract.Event) -> Unit
) {
    val isError = value.isNotEmpty() && value.length < 8

    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.all { it.isDigit() } && newValue.length <= 12) {
                onEvent(EditAlbumProfileContract.Event.OnKomcaNumberChanged(newValue))
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues()),
        label = { Text("KOMCA") },
        placeholder = { Text("12345678") },
        prefix = { Text("№ ") },
        trailingIcon = {
            IconButton(onClick = { onEvent(EditAlbumProfileContract.Event.OnKomcaScannerClicked) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_scanner),
                    contentDescription = null
                )
            }
        },
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun UserNotesField(
    value: String,
    onEvent: (EditAlbumProfileContract.Event) -> Unit
) {
    val maxChar = 2000

    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length <= maxChar) {
                onEvent(EditAlbumProfileContract.Event.OnUserNotesChanged(it))
            }
        },
        placeholder = { Text(stringResource(R.string.album_notes_placeholder)) },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp, max = 300.dp),
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

@Composable
private fun DialogHost(
    dialogState: EditAlbumProfileDialogState,
    onEvent: (EditAlbumProfileContract.Event) -> Unit,
    onRequestPermission: () -> Unit
) {
    when (dialogState) {
        is EditAlbumProfileDialogState.CameraRationale -> {
            CameraRationaleDialog {
                onEvent(EditAlbumProfileContract.Event.OnDismissDialog)
                onRequestPermission()
            }
        }

        is EditAlbumProfileDialogState.None -> {}
    }
}

private enum class Pending {
    CAMERA, SCANNER, NONE
}
