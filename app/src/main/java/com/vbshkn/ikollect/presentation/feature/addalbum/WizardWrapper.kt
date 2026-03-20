package com.vbshkn.ikollect.presentation.feature.addalbum

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.dialog.ConfirmDialog
import com.vbshkn.ikollect.presentation.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WizardWrapper(
    title: String,
    currentRoute: Route.AddAlbumFlow,
    viewModel: AddAlbumViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onExit: () -> Unit,
    isLastScreen: Boolean = false,
    content: @Composable ((PaddingValues) -> Unit)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DialogHost(
        dialogState = uiState.dialogState,
        onExitConfirmed = onExit,
        onDismiss = { viewModel.dismissDialog() }
    )
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.showDialog(AddAlbumDialogState.ConfirmExitDialog) }) {
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
    onExitConfirmed: () -> Unit,
    onDismiss: () -> Unit
) {
    when(dialogState) {
        AddAlbumDialogState.ConfirmExitDialog -> {
            ConfirmDialog(
                onConfirm = onExitConfirmed,
                onDismiss = onDismiss,
                title = stringResource(R.string.dialog_title_exit),
                text = stringResource(R.string.dialog_body_unsaved_data),
                action = stringResource(R.string.dialog_action_yes)
            )
        }
        AddAlbumDialogState.None -> {}
    }
}