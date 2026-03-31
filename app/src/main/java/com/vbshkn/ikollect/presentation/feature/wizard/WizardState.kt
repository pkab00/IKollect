package com.vbshkn.ikollect.presentation.feature.wizard

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.vbshkn.ikollect.util.UiText

class WizardState(
    val steps: List<WizardStep>,
    private val onFinish: () -> Unit,
    private val onExit: () -> Unit
) {
    var currentStepIndex by mutableIntStateOf(0)
        private set
    val currentStep get() = steps[currentStepIndex]
    val isFirstStep get() = currentStepIndex == 0
    val isLastStep get() = currentStepIndex == steps.size - 1

    fun next() {
        if (isLastStep) onFinish()
        else currentStepIndex++
    }

    fun back() {
        if (!isFirstStep) currentStepIndex--
        else onExit()
    }

    fun exit() = onExit()
}

@Composable
fun rememberWizardState(
    steps: List<WizardStep>,
    onFinish: () -> Unit,
    onExit: () -> Unit
) : WizardState {
    return remember { WizardState(steps, onFinish, onExit) }
}

interface WizardStep {
    val title: UiText
    @Composable fun isNextEnabled(): Boolean
    @Composable fun Content(paddingValues: PaddingValues)
}