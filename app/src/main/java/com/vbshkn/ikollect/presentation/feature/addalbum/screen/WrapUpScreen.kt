package com.vbshkn.ikollect.presentation.feature.addalbum.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import com.vbshkn.ikollect.presentation.composable.WizardItemWrapper
import com.vbshkn.ikollect.presentation.feature.addalbum.AddAlbumContract
import com.vbshkn.ikollect.presentation.feature.addalbum.AddAlbumViewModel

@Composable
fun WrapUpScreen(
    viewModel: AddAlbumViewModel,
    paddingValues: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(top = 16.dp)
    ) {
        item {
            WizardItemWrapper(title = stringResource(R.string.wizard_title_notes)) {
                UserNotesField(
                    value = uiState.albumCandidate.userNote,
                    onValueChange = {
                        viewModel.onEvent(AddAlbumContract.Event.OnUserNotesChanged(it))
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
        placeholder = { Text(stringResource(R.string.placeholder_notes)) },
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