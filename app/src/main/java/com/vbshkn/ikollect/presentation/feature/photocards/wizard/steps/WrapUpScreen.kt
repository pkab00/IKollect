package com.vbshkn.ikollect.presentation.feature.photocards.wizard.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardContract
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardContract
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardUIState
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardViewModel
import com.vbshkn.ikollect.presentation.feature.wizard.WizardItemWrapper
import com.vbshkn.ikollect.util.UiText

@Composable
fun WrapUpScreen(viewModel: PhotocardWizardViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            WizardItemWrapper(
                title = UiText.StringResource(R.string.photocard_wizard_subtitle_display_name),
                content = { PhotocardDisplayName(uiState, viewModel::onEvent) }
            )
        }
    }
}

@Composable
private fun PhotocardDisplayName(
    uiState: PhotocardWizardUIState,
    onEvent: (PhotocardWizardContract.Event) -> Unit
) {
    val isError = uiState.photocardCandidate.displayName.length > 100

    OutlinedTextField(
        value = uiState.photocardCandidate.displayName,
        onValueChange = { newValue ->
            onEvent(PhotocardWizardContract.Event.OnDisplayedNameChanged(newValue))
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues()),

        supportingText = {
            if (isError) {
                Text(
                    text = stringResource(R.string.textfield_text_too_long),
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(stringResource(R.string.photocard_name_field_suppoting_text))
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