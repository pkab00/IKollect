package com.vbshkn.ikollect.presentation.feature.albums.wizard.steps

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardViewModel
import com.vbshkn.ikollect.presentation.feature.wizard.WizardStep
import com.vbshkn.ikollect.presentation.navigation.Route
import com.vbshkn.ikollect.util.UiText

sealed interface AlbumWizardSteps {
    class SeeInfoStep(val viewModel: AlbumWizardViewModel) : WizardStep {
        override val title: UiText = UiText.StringResource(R.string.wizard_title_info)
        @Composable
        override fun isNextEnabled(): Boolean {
            return true
        }
        @Composable
        override fun Content(paddingValues: PaddingValues) {
            SeeInfoScreen(viewModel, paddingValues)
        }
    }

    class SelectVersionStep(val viewModel: AlbumWizardViewModel) : WizardStep {
        override val title: UiText = UiText.StringResource(R.string.wizard_title_version)
        @Composable
        override fun isNextEnabled(): Boolean {
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            return state.versionCandidate != null
        }
        @Composable
        override fun Content(paddingValues: PaddingValues) {
            SelectVersionScreen(viewModel, paddingValues)
        }
    }

    class AddDetailsStep(val viewModel: AlbumWizardViewModel) : WizardStep {
        override val title: UiText = UiText.StringResource(R.string.wizard_title_details)
        @Composable
        override fun isNextEnabled(): Boolean {
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val candidate = state.versionCandidate
            return candidate?.coverImage != null && candidate.name.isNotBlank()
        }
        @Composable
        override fun Content(paddingValues: PaddingValues) {
            AddDetailsScreen(viewModel, paddingValues)
        }
    }

    class WrapUpStep(val viewModel: AlbumWizardViewModel) : WizardStep {
        override val title: UiText = UiText.StringResource(R.string.wizard_title_wrapup)
        @Composable
        override fun isNextEnabled(): Boolean {
            return true
        }
        @Composable
        override fun Content(paddingValues: PaddingValues) {
            WrapUpScreen(viewModel, paddingValues)
        }
    }
}