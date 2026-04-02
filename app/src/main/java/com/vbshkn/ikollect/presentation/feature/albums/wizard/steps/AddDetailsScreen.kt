package com.vbshkn.ikollect.presentation.feature.albums.wizard.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.feature.wizard.WizardItemWrapper
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardContract
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardViewModel
import com.vbshkn.ikollect.presentation.feature.wizard.ImageSelectorPreview
import com.vbshkn.ikollect.presentation.feature.wizard.WizardImageSelector
import com.vbshkn.ikollect.util.UiText

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddDetailsScreen(viewModel: AlbumWizardViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showVersionNameField = remember { uiState.versionCandidate?.name?.isBlank() == true }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (showVersionNameField) {
            item {
                WizardItemWrapper(UiText.StringResource(R.string.wizard_title_version_name)) {
                    VersionNameField(
                        value = uiState.versionCandidate!!.name,
                        onValueChange = {
                            viewModel.onEvent(AlbumWizardContract.Event.OnVersionNameChanged(it))
                        }
                    )
                }
            }
        }

        item {
            WizardItemWrapper(UiText.StringResource(R.string.add_details_title_image)) {
                WizardImageSelector(
                    imageUrl = uiState.versionCandidate!!.coverImage,
                    onSelectPicture = { viewModel.onEvent(AlbumWizardContract.Event.OnSelectPicture) },
                    onTakePicture = { viewModel.onEvent(AlbumWizardContract.Event.OnTakePicture) }
                ) { url ->
                    ImageSelectorPreview(
                        imageUrl = url,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(8.dp)
                    )
                }
            }
        }

        item {
            WizardItemWrapper(
                title = UiText.StringResource(R.string.add_details_title_komca),
                showHint = true,
                onHint = { viewModel.onEvent(AlbumWizardContract.Event.OnShowKomcaHint) }
            ) {
                val text = uiState.komcaNumber
                KomcaNumberField(
                    value = text ?: "",
                    onValueChange = {
                        viewModel.onEvent(AlbumWizardContract.Event.OnKomcaCodeChanged(it))
                    },
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}

@Composable
fun VersionNameField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val limit = 100
    val isError = value.isNotEmpty() && value.length >= limit

    OutlinedTextField(
        value = value,
        onValueChange = { if (it.length <= limit) onValueChange(it) },
        placeholder = { Text("Digipack Ver. (CD)") },
        supportingText = { Text(stringResource(R.string.supporting_text_version)) },
        isError = isError,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues())
    )
}

@Composable
fun KomcaNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    onEvent: (AlbumWizardContract.Event) -> Unit
) {
    val isError = value.isNotEmpty() && value.length < 8

    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.all { it.isDigit() } && newValue.length <= 12) {
                onValueChange(newValue)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues()),
        label = { Text("KOMCA") },
        placeholder = { Text("12345678") },
        prefix = { Text("№ ") },
        trailingIcon = {
            IconButton(onClick = { onEvent(AlbumWizardContract.Event.OnScanKomca) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_scanner),
                    contentDescription = null
                )
            }
        },
        supportingText = {
            if (isError) {
                Text(
                    text = stringResource(R.string.komca_textfield_number_too_short),
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(stringResource(R.string.komca_textfield_supporting_text))
            }
        },
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}