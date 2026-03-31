package com.vbshkn.ikollect.presentation.feature.photocards.wizard.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.vbshkn.ikollect.presentation.feature.wizard.WizardScaffold
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardViewModel
import com.vbshkn.ikollect.util.UiText

@Composable
fun WizardWrapper(
    viewModel: PhotocardWizardViewModel,
    content: @Composable ((PaddingValues) -> Unit)
) {
    WizardScaffold(
        title = UiText.DynamicString("I don't know"),
        onExit = {},
        onBack = {},
        onNext = {},
        backEnabled = true,
        nextEnabled = true,
        isLastScreen = false,
    ) { paddingValues -> content(paddingValues) }
}