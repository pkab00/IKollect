package com.vbshkn.ikollect.presentation.feature.photocards.wizard.steps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardViewModel
import com.vbshkn.ikollect.presentation.feature.wizard.WizardStep
import com.vbshkn.ikollect.util.UiText

sealed interface PhotocardWizardSteps {
    class SelectPhotoStep(val viewModel: PhotocardWizardViewModel) : WizardStep {
        override val title: UiText = UiText.StringResource(R.string.photocard_wizard_title_select_photo)
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
        override val title: UiText = UiText.StringResource(R.string.photocard_wizard_title_select_owner)
        @Composable
        override fun isNextEnabled(): Boolean {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            return uiState.photocardCandidate.ownerId != null
        }
        @Composable
        override fun Content() {
            SelectArtistScreen(viewModel)
        }
    }

    class SelectMembersOptionalStep(val viewModel: PhotocardWizardViewModel) : WizardStep {
        override val title: UiText =  UiText.StringResource(R.string.photocard_wizard_title_select_members)
        @Composable
        override fun isNextEnabled(): Boolean {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            return uiState.photocardCandidate.depictedArtistsId.isNotEmpty()
        }
        @Composable
        override fun Content() {
            SelectMembersScreen(viewModel)
        }
    }

    class SelectAlbumStep(val viewModel: PhotocardWizardViewModel) : WizardStep {
        override val title: UiText = UiText.StringResource(R.string.photocard_wizard_title_select_album)
        @Composable
        override fun isNextEnabled(): Boolean {
            return true
        }
        @Composable
        override fun Content() {
            SelectAlbumScreen(viewModel)
        }
    }

    class AddDetailsStep(val viewModel: PhotocardWizardViewModel) : WizardStep {
        override val title: UiText = UiText.StringResource(R.string.photocard_wizard_title_wrap_up)
        @Composable
        override fun isNextEnabled(): Boolean {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            return !uiState.photocardCandidate.displayName.isNullOrBlank()
        }
        @Composable
        override fun Content() {
            WrapUpScreen(viewModel)
        }
    }
}