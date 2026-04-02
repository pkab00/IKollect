package com.vbshkn.ikollect.presentation.feature.wizard.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.dialog.InfoDialog

@Composable
fun CameraRationaleDialog(
    onDismiss: () -> Unit
) {
    InfoDialog(
        title = stringResource(R.string.dialog_title_request_camera),
        text = stringResource(R.string.dialog_body_request_camera),
        onDismiss = onDismiss
    )
}