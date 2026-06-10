package com.vbshkn.ikollect.presentation.feature.photocards.list

import com.vbshkn.ikollect.domain.model.TagItem

sealed interface PhotocardsContract {
    sealed interface Effect {
        object GoToWizard : Effect
        data class GoToPhotocard(val id: Long) : Effect
        object GoToSearch : Effect
        object ShowRefreshingErrorToast : Effect

    }
    sealed interface Event {
        object OnWizardClicked : Event
        data class OnPhotocardClicked(val id: Long) : Event
        data class OnPhotocardPreviewPressed(val imageUrl: String?) : Event
        data class OnTagSelected(val tag: TagItem) : Event
        object OnPhotocardPreviewReleased : Event
        object OnSearchClicked : Event
        object OnPulledToRefresh : Event
    }
}