package com.vbshkn.ikollect.presentation.feature.photocards.wizard

import android.Manifest
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.steps.PhotocardWizardSteps
import com.vbshkn.ikollect.presentation.feature.wizard.GenericWizard
import com.vbshkn.ikollect.presentation.feature.wizard.dialog.CameraRationaleDialog
import com.vbshkn.ikollect.presentation.feature.wizard.dialog.ExitWizardDialog
import com.vbshkn.ikollect.presentation.feature.wizard.rememberWizardState
import kotlin.collections.buildList

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PhotocardWizardScreen(
    viewModel: PhotocardWizardViewModel,
    onExit: () -> Unit,
    onCamera: () -> Unit,
    savedStateHandle: SavedStateHandle
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val steps = remember(uiState) {
        buildList {
            add(PhotocardWizardSteps.SelectPhotoStep(viewModel))
            add(PhotocardWizardSteps.SelectArtistStep(viewModel))
            add(PhotocardWizardSteps.SelectAlbumStep(viewModel))
            if (uiState.photocardCandidate.owner?.isGroup == true) {
                add(PhotocardWizardSteps.WhoIsOnTheCardOptional(viewModel))
            }
            add(PhotocardWizardSteps.AddDetailsStep(viewModel))
        }
    }
    val wizardState = rememberWizardState(
        steps = steps,
        initialStepIndex = uiState.currentStep,
        onStepChanged = { viewModel.onEvent(PhotocardWizardContract.Event.OnStepChanged(it)) },
        onFinish = { onExit() },
        onExit = { viewModel.onEvent(PhotocardWizardContract.Event.OnExitClicked) }
    )

    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var pending by rememberSaveable { mutableStateOf(false) }


    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                viewModel.onEvent(PhotocardWizardContract.Event.OnPhotoSelected(uri.toString()))
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                PhotocardWizardContract.Effect.NavigateBack -> wizardState.back()
                PhotocardWizardContract.Effect.NavigateNext -> wizardState.next()
                PhotocardWizardContract.Effect.Exit -> onExit()
                PhotocardWizardContract.Effect.OpenGallery -> {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
                PhotocardWizardContract.Effect.TryOpenCamera -> {
                    when {
                        cameraPermissionState.status.isGranted -> {
                            onCamera()
                        }
                        cameraPermissionState.status.shouldShowRationale -> {
                            pending = true
                            viewModel.onEvent(PhotocardWizardContract.Event.OnShowCameraRationale)
                        }
                        else -> {
                            pending = true
                            cameraPermissionState.launchPermissionRequest()
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(cameraPermissionState.status) {
        if (cameraPermissionState.status.isGranted && pending) {
            pending = false
            onCamera()
        }
    }

    CameraResultObserver(
        savedStateHandle = savedStateHandle,
        viewModel = viewModel
    )
    DialogHost(
        dialogState = uiState.dialogState,
        onEvent = viewModel::onEvent,
        onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
    )
    GenericWizard(wizardState)
}

@Composable
private fun CameraResultObserver(
    savedStateHandle: SavedStateHandle,
    viewModel: PhotocardWizardViewModel
) {
    val cameraResult by savedStateHandle.getStateFlow<String?>("camera_result", null)
        .collectAsStateWithLifecycle()

    LaunchedEffect(cameraResult) {
        if (cameraResult != null) {
            viewModel.onEvent(PhotocardWizardContract.Event.OnPhotoSelected(cameraResult!!))
            savedStateHandle["camera_result"] = null
        }
    }
}

@Composable
private fun DialogHost(
    dialogState: PhotocardWizardDialogState,
    onEvent: (PhotocardWizardContract.Event) -> Unit,
    onRequestPermission: () -> Unit
) {
    when (dialogState) {
        PhotocardWizardDialogState.ExitDialog -> {
            ExitWizardDialog(
                onConfirm = { onEvent(PhotocardWizardContract.Event.OnExitConfirmed) },
                onDismiss = { onEvent(PhotocardWizardContract.Event.OnDismissDialog) }
            )
        }
        PhotocardWizardDialogState.CameraRationale -> {
            CameraRationaleDialog {
                onEvent(PhotocardWizardContract.Event.OnDismissDialog)
                onRequestPermission()
            }
        }
        PhotocardWizardDialogState.None -> {}
    }
}