package com.vbshkn.ikollect.presentation.feature.camera.barcode_scanner

import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Message
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.business.BarcodeAnalyzer
import com.vbshkn.ikollect.domain.model.candidate.AlbumCandidate
import com.vbshkn.ikollect.presentation.composable.CameraPreview
import com.vbshkn.ikollect.presentation.composable.LoadingOverlay
import com.vbshkn.ikollect.presentation.composable.dialog.ConfirmDialog
import com.vbshkn.ikollect.presentation.composable.dialog.ErrorDialog
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsContract.Event
import com.vbshkn.ikollect.util.UiText
import kotlinx.coroutines.delay

@Composable
fun BarcodeScannerScreen(
    viewModel: BarcodeScannerViewModel,
    onAlbumCandidateConfirmed: (AlbumCandidate) -> Unit
) {
    var currentRecognizedCode by remember { mutableStateOf<String?>(null) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showDialog = uiState.dialogState !is BarcodeScannerDialogState.None

    val context = LocalContext.current
    val analyzer = remember {
        BarcodeAnalyzer { barcode ->
            if (currentRecognizedCode != barcode && !uiState.isLoading && !showDialog) {
                currentRecognizedCode = barcode
            }
        }
    }
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE or CameraController.IMAGE_ANALYSIS)
            setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(context), analyzer)
        }
    }

    LaunchedEffect(currentRecognizedCode) {
        if (currentRecognizedCode != null) {
            delay(1000)
            if (!showDialog) {
                viewModel.onBarcodeRecognized(currentRecognizedCode!!)
            }
        }
    }

    when (uiState.dialogState) {
        is BarcodeScannerDialogState.ErrorDialog -> {
            ErrorDialog(
                message = (uiState.dialogState as BarcodeScannerDialogState.ErrorDialog).message,
                onDismiss = {
                    viewModel.updateDialogState(BarcodeScannerDialogState.None)
                    currentRecognizedCode = null
                }
            )
        }
        is BarcodeScannerDialogState.SuccessDialog -> {
            ConfirmDialog(
                albumName = uiState.albumCandidate?.displayName ?: "",
                onConfirm = { uiState.albumCandidate?.let(onAlbumCandidateConfirmed) },
                onDismiss = {
                    viewModel.updateDialogState(BarcodeScannerDialogState.None)
                    currentRecognizedCode = null
                }
            )
        }
        is BarcodeScannerDialogState.None -> {}
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CameraPreview(controller, Modifier.fillMaxSize())
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .border(2.dp, MaterialTheme.colorScheme.outline)
                    .align(Alignment.Center)
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp)
            ) {
                Text(
                    text = stringResource(R.string.barcode_scanner_tip),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
            currentRecognizedCode?.let {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 64.dp)
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
    if (uiState.isLoading) LoadingOverlay()
}

@Composable
private fun ConfirmDialog(
    albumName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmDialog(
        title = stringResource(R.string.dialog_title_album_detected),
        text = albumName,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

@Composable
private fun ErrorDialog(
    message: UiText,
    onDismiss: () -> Unit
) {
    ErrorDialog(
        title = stringResource(R.string.error_title_scanning),
        errorMessage = message.asString(),
        onDismiss = onDismiss,
    )
}