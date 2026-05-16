package com.vbshkn.ikollect.presentation.composable.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vbshkn.ikollect.R

@Composable
fun TextInputDialog(
    title: String,
    isError: Boolean = false,
    errorSupportingText: String? = null,
    onValueChanged: (String) -> Unit,
    onConfirm: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var textInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = title)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = textInput,
                    isError = isError,
                    supportingText = {
                        if (isError && errorSupportingText != null) {
                            Text(
                                text = errorSupportingText,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    onValueChange = {
                        textInput = it
                        onValueChanged(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isError,
                onClick = {
                    onConfirm(textInput)
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.dialog_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.dialog_dismiss))
            }
        }
    )
}