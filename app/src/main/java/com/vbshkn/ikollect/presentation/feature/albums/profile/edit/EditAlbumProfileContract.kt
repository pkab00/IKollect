package com.vbshkn.ikollect.presentation.feature.albums.profile.edit

import com.vbshkn.ikollect.domain.model.UserItemImage

sealed interface EditAlbumProfileContract {
    sealed interface Effect {
        object NavigateBack : Effect
        object TryOpenScanner : Effect
        object TryOpenCamera : Effect
        object OpenGallery : Effect
    }

    sealed interface Event {
        object OnBackClicked : Event
        object OnOpenGalleryClicked : Event
        object OnOpenCameraClicked : Event
        object OnSaveChangesClicked : Event
        data class OnImageChanged(val image: UserItemImage?) : Event
        data class OnAlbumNameChanged(val name: String) : Event
        data class OnAlbumVersionChanged(val version: String) : Event
        data class OnKomcaNumberChanged(val number: String) : Event
        data class OnUserNotesChanged(val notes: String) : Event
        object OnKomcaScannerClicked : Event
        object OnShowCameraRationale : Event
        object OnDismissDialog : Event
    }
}
