package com.vbshkn.ikollect.presentation.feature.wizard

import androidx.compose.runtime.Composable

@Composable
fun GenericWizard(
    state: WizardState
) {
    WizardScaffold(
        title = state.currentStep.title,
        onExit = { state.exit() },
        onBack = { state.back() },
        onNext = { state.next() },
        backEnabled = !state.isLastStep,
        nextEnabled = state.currentStep.isNextEnabled(),
        isLastScreen = state.isLastStep
    ) { paddingValues -> state.currentStep.Content(paddingValues) }
}