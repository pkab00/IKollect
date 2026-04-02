package com.vbshkn.ikollect.presentation.feature.wizard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GenericWizard(
    state: WizardState
) {
    WizardScaffold(
        title = state.currentStep.title,
        stepNumber = state.currentStepIndex + 1,
        totalSteps = state.steps.size,
        onExit = { state.exit() },
        onBack = { state.back() },
        onNext = { state.next() },
        backEnabled = !state.isFirstStep,
        nextEnabled = state.currentStep.isNextEnabled(),
        isLastScreen = state.isLastStep
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 16.dp)
        ) {
            state.currentStep.Content()
        }
    }
}