package com.vbshkn.ikollect.presentation.feature.photocards.profile.edit

sealed interface EditPhotocardProfileContract {
    sealed interface Effect {
        object NavigateBack : Effect
        object OpenGallery : Effect
    }

    sealed interface Event {
        object OnBackClicked : Event
        object OnOpenGalleryClicked : Event
        object OnSaveChangesClicked : Event
        data class OnImageChanged(val image: String?) : Event
        data class OnPhotocardNameChanged(val name: String) : Event
        data class OnUserNotesChanged(val notes: String) : Event
        data class OnTagClick(val tagId: Long) : Event
        object OnSelectTagsClick : Event
        object OnDismissTagSelector : Event
        object OnShowCameraRationale : Event
        object OnDismissDialog : Event
    }
}

