package com.vbshkn.ikollect.presentation.feature.photocards.wizard.steps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardViewModel
import com.vbshkn.ikollect.presentation.feature.wizard.WizardStep
import com.vbshkn.ikollect.util.UiText

sealed interface PhotocardWizardSteps {
    class SelectPhotoStep(val viewModel: PhotocardWizardViewModel) : WizardStep {
        override val title: UiText = UiText.DynamicString("Раз")
        @Composable
        override fun isNextEnabled(): Boolean {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            return uiState.photocardCandidate.imageUrl != null
        }
        @Composable
        override fun Content() {
            SelectPhotoScreen(viewModel)
        }
    }

    class SelectArtistStep(val viewModel: PhotocardWizardViewModel) : WizardStep {
        override val title: UiText = UiText.DynamicString("Два")
        @Composable
        override fun isNextEnabled(): Boolean {
            return true
        }
        @Composable
        override fun Content() {
            // TODO
        }
    }

    class SelectAlbumStep(val viewModel: PhotocardWizardViewModel) : WizardStep {
        override val title: UiText = UiText.DynamicString("Три")
        @Composable
        override fun isNextEnabled(): Boolean {
            return true
        }
        @Composable
        override fun Content() {
            // TODO
        }
    }

    class WhoIsOnTheCardOptional(val viewModel: PhotocardWizardViewModel) : WizardStep {
        override val title: UiText = UiText.DynamicString("Четыре")
        @Composable
        override fun isNextEnabled(): Boolean {
            return true
        }
        @Composable
        override fun Content() {
            // TODO
        }
    }

    class AddDetailsStep(val viewModel: PhotocardWizardViewModel) : WizardStep {
        override val title: UiText = UiText.DynamicString("Пять")
        @Composable
        override fun isNextEnabled(): Boolean {
            return true
        }
        @Composable
        override fun Content() {
            // TODO
        }
    }
}