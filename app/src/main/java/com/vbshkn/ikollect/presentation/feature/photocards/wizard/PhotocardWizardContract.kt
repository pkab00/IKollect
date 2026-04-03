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
        data class OnUpdateOwner(val id: Long, val isGroup: Boolean) : Event
        data class OnUpdateDepictedIds(val ids: List<Long>) : Event
    }
}