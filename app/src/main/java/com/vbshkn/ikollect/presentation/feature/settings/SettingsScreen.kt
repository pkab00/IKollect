package com.vbshkn.ikollect.presentation.feature.settings

import com.vbshkn.ikollect.presentation.feature.settings.SettingsContract.Event
import com.vbshkn.ikollect.presentation.feature.settings.SettingsContract.Effect
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.composable.LoadingOverlay
import com.vbshkn.ikollect.presentation.composable.dialog.ConfirmDialog
import com.vbshkn.ikollect.presentation.composable.dialog.TextInputDialog
import com.vbshkn.ikollect.presentation.feature.auth.nicknameErrorHandler

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                Effect.GoBack -> onNavigateBack()
            }
        }
    }

    DialogHost(uiState, viewModel::onEvent)
    Scaffold(
        topBar = { SettingsTopBar(viewModel::onEvent) },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.user != null) {
                item {
                    SettingsContainer {
                        SettingsCategoryHeader(title = stringResource(R.string.settings_section_account))
                        ClickableSettingItem(
                            title = stringResource(R.string.settings_item_change_username),
                            onClick = { viewModel.onEvent(Event.OnChangeNicknameClicked) }
                        )
                        ClickableSettingItem(
                            title = stringResource(R.string.settings_item_log_out),
                            trailingIcon = Icons.AutoMirrored.Filled.Logout,
                            onClick = { viewModel.onEvent(Event.OnLogOutClicked) }
                        )
                    }
                }
            }
        }
    }
    if (uiState.isLoading) {
        LoadingOverlay()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(
    onEvent: (Event) -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.title_settings),
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = { onEvent(Event.OnBackClicked) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
fun SettingsContainer(
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(6.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
private fun SettingsCategoryHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun ClickableSettingItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    ListItem(
        headlineContent = { Text(text = title) },
        supportingContent = subtitle?.let { { Text(text = it) } },
        leadingContent = leadingIcon?.let {
            { Icon(imageVector = it, contentDescription = null) }
        },
        trailingContent = trailingIcon?.let {
            { Icon(imageVector = it, contentDescription = null) }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun SwitchSettingItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null
) {
    ListItem(
        headlineContent = { Text(text = title) },
        supportingContent = subtitle?.let { { Text(text = it) } },
        leadingContent = leadingIcon?.let {
            { Icon(imageVector = it, contentDescription = null) }
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = modifier.clickable { onCheckedChange(!checked) }
    )
}

@Composable
private fun DialogHost(
    uiState: SettingsUIState,
    onEvent: (Event) -> Unit
) {
    when (uiState.dialogState) {
        is SettingsDialogState.ConfirmLogOutDialog -> {
            ConfirmDialog(
                title = stringResource(R.string.confirm_exit_dialog_title),
                text = stringResource(R.string.confirm_exit_dialog_body),
                onConfirm = { onEvent(Event.OnLogOutConfirmed) },
                onDismiss = { onEvent(Event.OnDismissDialog) },
            )
        }

        is SettingsDialogState.NewNicknameDialog -> {
            TextInputDialog(
                title = stringResource(R.string.dialog_title_new_nickname),
                isError = uiState.nicknameValidationError != null,
                errorSupportingText = uiState.nicknameValidationError?.let { nicknameErrorHandler(it).asString() },
                onValueChanged = { onEvent(Event.OnNicknameFieldChanged(it)) },
                onConfirm = { onEvent(Event.OnNewNicknameSelected(it)) },
                onDismissRequest = { onEvent(Event.OnDismissDialog) }
            )
        }

        is SettingsDialogState.None -> {}
    }
}