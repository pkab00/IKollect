package com.vbshkn.ikollect.presentation.composable.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vbshkn.ikollect.R

@Composable
fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmDialog(
        title = stringResource(R.string.confirm_delete_dialog_title),
        text = stringResource(R.string.confirm_delete_dialog_body),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}