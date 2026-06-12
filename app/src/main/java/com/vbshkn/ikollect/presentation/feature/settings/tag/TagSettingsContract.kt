package com.vbshkn.ikollect.presentation.feature.settings.tag

import com.vbshkn.ikollect.domain.model.TagItem

sealed interface TagSettingsContract {
    sealed interface Effect {
        data object NavigateBack : Effect
    }
    sealed interface Event {
        data object OnNavigateBackClicked : Event
        data class OnCustomTagSelected(val tag: TagItem) : Event
        data object OnNewTagClicked : Event
        data object OnDismissDialogClicked : Event
        data class OnSaveNewTagConfirmed(val tag: TagItem) : Event
        data class OnEditTagConfirmed(val tag: TagItem) : Event
    }
}