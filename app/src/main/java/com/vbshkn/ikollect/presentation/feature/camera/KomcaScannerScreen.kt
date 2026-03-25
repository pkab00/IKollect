package com.vbshkn.ikollect.presentation.feature.camera

import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.business.KomcaAnalyzer
import com.vbshkn.ikollect.presentation.composable.CameraPreview
import com.vbshkn.ikollect.presentation.dialog.ConfirmDialog
import kotlinx.coroutines.delay

@Composable
fun KomcaScannerScreen(
    onNumberRecognized: (String) -> Unit
) {
    var recognizedValue by remember { mutableStateOf<String?>(null) }
    var fixedValue by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(recognizedValue) {
        if (recognizedValue != null) {
            delay(1000)
            if (!showDialog) {
                fixedValue = recognizedValue
                showDialog = true
            }
        }
    }

    val context = LocalContext.current
    val analyzer = KomcaAnalyzer { komca ->
        if (recognizedValue != komca) {
            recognizedValue = komca
        }
    }
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE or CameraController.IMAGE_ANALYSIS)
            setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(context), analyzer)
        }
    }


    ConfirmNumberDialog(
        show = showDialog && fixedValue != null,
        number = fixedValue ?: "",
        onConfirm = onNumberRecognized,
        onDismiss = {
            showDialog = false
            recognizedValue = null
            fixedValue = null
        }
    )

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
                    text = stringResource(R.string.komca_scanner_tip),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
            recognizedValue?.let {
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
}

@Composable
private fun ConfirmNumberDialog(
    show: Boolean,
    number: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (show) {
        ConfirmDialog(
            title = number,
            text = stringResource(R.string.komca_scanner_confirm),
            onConfirm = { onConfirm(number) },
            onDismiss = onDismiss
        )
    }
}