package com.vbshkn.ikollect.presentation.feature.photocards.profile.edit

import com.vbshkn.ikollect.presentation.feature.photocards.profile.edit.EditPhotocardProfileContract.Event
import com.vbshkn.ikollect.presentation.feature.photocards.profile.edit.EditPhotocardProfileContract.Effect
import android.Manifest
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.composable.ImageChangerItem
import com.vbshkn.ikollect.presentation.composable.PlainTextField
import com.vbshkn.ikollect.presentation.feature.wizard.WizardItemWrapper
import com.vbshkn.ikollect.presentation.feature.wizard.dialog.CameraRationaleDialog
import com.vbshkn.ikollect.util.UiText
import com.vbshkn.ikollect.presentation.composable.TagSelectionSheet
import com.vbshkn.ikollect.presentation.composable.TagsField

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EditPhotocardProfileScreen(
    viewModel: EditPhotocardProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                } catch (_: SecurityException) {
                    // Some content providers don't support persistable URI permissions
                    // App will still work with temporary URI permissions
                }
                viewModel.onEvent(EditPhotocardProfileContract.Event.OnImageChanged(uri.toString()))
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is Effect.NavigateBack -> onNavigateBack()
                is Effect.OpenGallery -> galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }

    DialogHost(
        dialogState = uiState.dialogState,
        onEvent = viewModel::onEvent,
        onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
    )

    TagSelectionSheet(
        allTags = uiState.allTags,
        selectedTagIds = uiState.selectedTagIds,
        enabled = uiState.enableTagSelector,
        onTagClick = { viewModel.onEvent(Event.OnTagClick(it)) },
        onDismiss = { viewModel.onEvent(Event.OnDismissTagSelector) }
    )

    Scaffold(
        topBar = { TopBar(viewModel::onEvent) }
    ) { paddingValues ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 8.dp)
        ) {
            item {
                ImageChangerItem(
                    title = stringResource(R.string.title_change_image),
                    imageUrl = uiState.image,
                    blurRadius = 60.dp,
                    onClick = { viewModel.onEvent(Event.OnOpenGalleryClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            item {
                WizardItemWrapper(UiText.StringResource(R.string.album_profile_label_title)) {
                    PhotocardNameField(value = uiState.photocardName, onEvent = viewModel::onEvent)
                }
            }

            item {
                WizardItemWrapper(UiText.StringResource(R.string.profile_label_tags)) {
                    TagsField(
                        tags = uiState.allTags.filter { it.id in uiState.selectedTagIds },
                        onTagClick = { viewModel.onEvent(Event.OnTagClick(it)) },
                        displaySelectTagsButton = true,
                        onSelectTagsClick = { viewModel.onEvent(Event.OnSelectTagsClick) }
                    )
                }
            }

            item {
                WizardItemWrapper(UiText.StringResource(R.string.profile_title_notes)) {
                    UserNotesField(value = uiState.userNotes, onEvent = viewModel::onEvent)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onEvent: (Event) -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.title_edit_photocard),
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
        actions = {
            IconButton(onClick = { onEvent(Event.OnSaveChangesClicked) }) {
                Icon(
                    imageVector = Icons.Default.Check,
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
fun PhotocardNameField(
    value: String,
    onEvent: (Event) -> Unit
) {
    PlainTextField(
        value = value,
        onValueChange = { onEvent(Event.OnPhotocardNameChanged(it)) },
        title = UiText.StringResource(R.string.album_profile_label_title),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
    )
}

@Composable
fun UserNotesField(
    value: String,
    onEvent: (Event) -> Unit
) {
    val maxChar = 2000

    OutlinedTextField(
        value = value,
        onValueChange = { if (it.length <= maxChar) { onEvent(Event.OnUserNotesChanged(it)) } },
        placeholder = { Text(stringResource(R.string.album_notes_placeholder)) },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp, max = 300.dp),
        minLines = 8,
        supportingText = {
            Text(
                text = "${value.length} / $maxChar",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodySmall
            )
        },
        isError = value.length >= maxChar,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun DialogHost(
    dialogState: EditPhotocardProfileDialogState,
    onEvent: (Event) -> Unit,
    onRequestPermission: () -> Unit
) {
    when (dialogState) {
        is EditPhotocardProfileDialogState.CameraRationale -> {
            CameraRationaleDialog {
                onEvent(Event.OnDismissDialog)
                onRequestPermission()
            }
        }
        is EditPhotocardProfileDialogState.None -> {}
    }
}








