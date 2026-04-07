package com.vbshkn.ikollect.presentation.feature.photocards.list

sealed interface PhotocardsContract {
    sealed interface Effect {
        object GoToWizard : Effect
    }
    sealed interface Event {
        object OnWizardClicked : Event
        data class OnPhotocardPreviewPressed(val imageUrl: String?) : Event
        object OnPhotocardPreviewReleased : Event
    }
}