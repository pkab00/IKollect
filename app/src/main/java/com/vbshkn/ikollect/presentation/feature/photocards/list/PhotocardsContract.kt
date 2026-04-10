package com.vbshkn.ikollect.presentation.feature.photocards.list

sealed interface PhotocardsContract {
    sealed interface Effect {
        object GoToWizard : Effect
        data class GoToPhotocard(val id: Long) : Effect
    }
    sealed interface Event {
        object OnWizardClicked : Event
        data class OnPhotocardClicked(val id: Long) : Event
        data class OnPhotocardPreviewPressed(val imageUrl: String?) : Event
        object OnPhotocardPreviewReleased : Event
    }
}