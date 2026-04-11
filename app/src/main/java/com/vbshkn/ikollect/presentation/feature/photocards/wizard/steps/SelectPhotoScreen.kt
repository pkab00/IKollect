package com.vbshkn.ikollect.presentation.feature.photocards.wizard.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardContract
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardViewModel
import com.vbshkn.ikollect.presentation.feature.wizard.ImageSelectorPreview
import com.vbshkn.ikollect.presentation.feature.wizard.WizardItemWrapper
import com.vbshkn.ikollect.presentation.feature.wizard.WizardImageSelector
import com.vbshkn.ikollect.util.UiText

@Composable
fun SelectPhotoScreen(viewModel: PhotocardWizardViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            WizardItemWrapper(UiText.StringResource(R.string.add_details_title_image)) {
                WizardImageSelector(
                    displayedImage = uiState.photocardCandidate.imageUrl,
                    imageOptions = uiState.photocardImagePreviews,
                    onSelectPicture = { viewModel.onEvent(PhotocardWizardContract.Event.OnOpenGallerySelector) },
                    onTakePicture = { viewModel.onEvent(PhotocardWizardContract.Event.OnOpenCamera) },
                    onImageClicked = { viewModel.onEvent(PhotocardWizardContract.Event.OnPhotocardPreviewSelected(it)) }
                ) { url ->
                    ImageSelectorPreview(
                        imageUrl = url,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}