package com.vbshkn.ikollect.presentation.feature.albums.wizard

import android.Manifest
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.dialog.ConfirmDialog
import com.vbshkn.ikollect.presentation.dialog.ErrorDialog
import com.vbshkn.ikollect.presentation.dialog.InfoDialog
import com.vbshkn.ikollect.presentation.feature.albums.wizard.steps.AlbumWizardSteps
import com.vbshkn.ikollect.presentation.feature.wizard.GenericWizard
import com.vbshkn.ikollect.presentation.feature.wizard.rememberWizardState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AlbumWizardScreen(
    viewModel: AlbumWizardViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onExit: () -> Unit,
    onCamera: () -> Unit = {},
    onScanner: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val onEvent: (AlbumWizardContract.Event) -> Unit = viewModel::onEvent

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
                viewModel.onEvent(AlbumWizardContract.Event.OnExistingPhotoSelected(uri.toString()))
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
                AlbumWizardContract.Effect.Exit -> onExit()
                AlbumWizardContract.Effect.NavigateBack -> onBack()
                AlbumWizardContract.Effect.NavigateNext -> onNext()
                AlbumWizardContract.Effect.OpenGallery -> {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                AlbumWizardContract.Effect.TryOpenCamera -> {
                    val status = cameraPermissionState.status
                    when {
                        status.isGranted -> {
                            onCamera()
                        }
                        status.shouldShowRationale -> {
                            pending = PENDING.CAMERA
                            onEvent(AlbumWizardContract.Event.OnShowCameraRationale)
                        }
                        else -> {
                            pending = PENDING.CAMERA
                            cameraPermissionState.launchPermissionRequest()
                        }
                    }
                }
                AlbumWizardContract.Effect.TryOpenScanner -> {
                    val status = cameraPermissionState.status
                    when {
                        status.isGranted -> {
                            onScanner()
                        }
                        status.shouldShowRationale -> {
                            pending = PENDING.SCANNER
                            onEvent(AlbumWizardContract.Event.OnShowCameraRationale)
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

    Wrapper(viewModel)

}

@Composable
fun Wrapper(viewModel: AlbumWizardViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val steps = remember { listOf(
        AlbumWizardSteps.SeeInfoStep(viewModel),
        AlbumWizardSteps.SelectVersionStep(viewModel),
        AlbumWizardSteps.AddDetailsStep(viewModel),
        AlbumWizardSteps.WrapUpStep(viewModel)
    ) }
    val wizardState = rememberWizardState(
        steps = steps,
        onFinish = { viewModel.onEvent(AlbumWizardContract.Event.OnWrapUp) },
        onExit = { viewModel.onEvent(AlbumWizardContract.Event.OnExitClicked) }
    )
    GenericWizard(wizardState)
}

@Composable
private fun DialogHost(
    dialogState: AlbumWizardDialogState,
    onEvent: (AlbumWizardContract.Event) -> Unit,
    onRequestPermission: () -> Unit
) {
    when(dialogState) {
        is AlbumWizardDialogState.ConfirmExitWizardDialog -> {
            ConfirmDialog(
                onConfirm = { onEvent(AlbumWizardContract.Event.OnExitConfirmed) },
                onDismiss = { onEvent(AlbumWizardContract.Event.OnDismissDialog) },
                title = stringResource(R.string.dialog_title_exit),
                text = stringResource(R.string.dialog_body_unsaved_data),
                action = stringResource(R.string.dialog_action_yes)
            )
        }
        is AlbumWizardDialogState.CameraRationaleWizardDialog -> {
            InfoDialog(
                title = stringResource(R.string.dialog_title_request_camera),
                text = stringResource(R.string.dialog_body_request_camera),
                onDismiss = {
                    onEvent(AlbumWizardContract.Event.OnDismissDialog)
                    onRequestPermission()
                }
            )
        }
        is AlbumWizardDialogState.CameraErrorWizardDialog -> {
            ErrorDialog(
                title = stringResource(R.string.dialog_title_failed_saving_photo),
                errorMessage = stringResource(R.string.dialog_body_failed_saving_photo),
                onDismiss = { onEvent(AlbumWizardContract.Event.OnDismissDialog) }
            )
        }
        is AlbumWizardDialogState.AboutKomcaWizardDialog -> {
            InfoDialog(
                title = stringResource(R.string.dialog_about_komca_title),
                text = stringResource(R.string.dialog_about_komca_body),
                onDismiss = { onEvent(AlbumWizardContract.Event.OnDismissDialog) }
            )
        }
        is AlbumWizardDialogState.None -> {}
    }
}

private enum class PENDING {
    CAMERA, SCANNER, NONE
}