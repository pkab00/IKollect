package com.vbshkn.ikollect.presentation.feature.wizard

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardContract
import com.vbshkn.ikollect.util.UiText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WizardScaffold(
    title: UiText,
    onExit: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    backEnabled: Boolean,
    nextEnabled: Boolean,
    isLastScreen: Boolean,
    content: @Composable ((PaddingValues) -> Unit)
) {
    BackHandler(enabled = true) {
        if (!backEnabled) onExit()
        else onBack()
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(title.asString()) },
                navigationIcon = {
                    IconButton(onClick = onExit) {
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
                        enabled = backEnabled
                    ) { Text(text = stringResource(R.string.wizard_action_back)) }
                    Button(
                        onClick = onNext,
                        enabled = nextEnabled
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