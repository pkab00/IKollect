package com.vbshkn.ikollect.presentation.feature.wizard.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.composable.dialog.ConfirmDialog

@Composable
fun ExitWizardDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmDialog(
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        title = stringResource(R.string.dialog_title_exit),
        text = stringResource(R.string.dialog_body_unsaved_data),
        action = stringResource(R.string.dialog_action_yes)
    )
}