package com.vbshkn.ikollect.presentation.feature.photocards.wizard

sealed interface PhotocardWizardContract {
    sealed interface Effect {
        object NavigateBack : Effect
        object NavigateNext : Effect
        object Exit : Effect
        object OpenGallery : Effect
        object TryOpenCamera : Effect
    }
    sealed interface Event {
        data class OnStepChanged(val newStep: Int) : Event
        object OnBackClicked : Event
        object OnNextClicked : Event
        object OnExitClicked : Event
        object OnExitConfirmed : Event
        object OnDismissDialog : Event
        object OnShowCameraRationale : Event
        object OnOpenGallerySelector : Event
        object OnOpenCamera : Event
        data class OnPhotoSelected(val uri: String) : Event
        object OnShowSelectArtistTip : Event
        data class OnOwnerSelected(val id: Long, val isGroup: Boolean) : Event
        data class OnMemberSelected(val ids: List<Long>) : Event
        data class OnAlbumSelected(val id: Long) : Event
        data class OnDisplayedNameChanged(val newName: String) : Event
        data object OnAddTagClicked : Event
        data class OnTagSelected(val tagId: Long) : Event
        data object OnDismissTagSelector : Event
        data class OnUserNotesChanged(val newValue: String) : Event
        data object OnFinish : Event
    }
}