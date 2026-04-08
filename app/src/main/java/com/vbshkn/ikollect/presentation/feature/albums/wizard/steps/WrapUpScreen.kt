package com.vbshkn.ikollect.presentation.feature.albums.wizard.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.feature.wizard.WizardItemWrapper
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardContract
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardViewModel
import com.vbshkn.ikollect.util.UiText

@Composable
fun WrapUpScreen(viewModel: AlbumWizardViewModel, ) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            WizardItemWrapper(title = UiText.StringResource(R.string.wizard_title_notes)) {
                UserNotesField(
                    value = uiState.albumCandidate?.userNote!!,
                    onValueChange = {
                        viewModel.onEvent(AlbumWizardContract.Event.OnUserNotesChanged(it))
                    }
                )
            }
        }
    }
}

@Composable
private fun UserNotesField(
    value: String,
    onValueChange: (String) -> Unit
) {
    val maxChar = 2000

    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length <= maxChar) onValueChange(it)
        },
        placeholder = { Text(stringResource(R.string.album_notes_placeholder)) },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp, max = 400.dp),
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