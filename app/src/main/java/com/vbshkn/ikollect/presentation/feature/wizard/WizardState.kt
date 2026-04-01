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
    private val initialStepIndex: Int,
    private val onStepChanged: (Int) -> Unit,
    private val onFinish: () -> Unit,
    private val onExit: () -> Unit
) {
    var currentStepIndex by mutableIntStateOf(initialStepIndex)
        private set
    val currentStep get() = steps[currentStepIndex]
    val isFirstStep get() = currentStepIndex == 0
    val isLastStep get() = currentStepIndex == steps.size - 1

    fun next() {
        if (isLastStep) onFinish()
        else {
            currentStepIndex++
            onStepChanged(currentStepIndex)
        }

    }

    fun back() {
        if (!isFirstStep) {
            currentStepIndex--
            onStepChanged(currentStepIndex)
        }
        else onExit()
    }

    fun exit() = onExit()
}

@Composable
fun rememberWizardState(
    steps: List<WizardStep>,
    initialStepIndex: Int,
    onStepChanged: (Int) -> Unit,
    onFinish: () -> Unit,
    onExit: () -> Unit

) : WizardState {
    return remember { WizardState(steps, initialStepIndex, onStepChanged, onFinish, onExit) }
}

interface WizardStep {
    val title: UiText
    @Composable fun isNextEnabled(): Boolean
    @Composable fun Content()
}