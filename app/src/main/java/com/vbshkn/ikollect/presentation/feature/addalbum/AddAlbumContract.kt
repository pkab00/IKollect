package com.vbshkn.ikollect.presentation.feature.addalbum

import com.vbshkn.ikollect.domain.model.VersionCandidate

interface AddAlbumContract {
    sealed interface Effect {
        object NavigateBack : Effect
        object NavigateNext : Effect
        object Exit : Effect
        object OpenGallery : Effect
        object TryOpenCamera : Effect
        object TryOpenScanner : Effect
    }
    sealed interface Event {
        object OnBackClicked : Event
        object OnNextClicked : Event
        object OnExitClicked : Event
        object OnExitConfirmed : Event
        object OnDismissDialog : Event
        object OnSelectPicture : Event
        object OnTakePicture : Event
        object OnScanKomca : Event
        object OnShowCameraRationale : Event
        object OnShowKomcaHint : Event
        data class OnPictureCaptured(val uri: String) : Event
        data class OnUpdateVersion(val candidate: VersionCandidate) : Event
        data class OnExistingPhotoSelected(val path: String) : Event
        data class OnKomcaCodeChanged(val newCode: String) : Event
        data class OnUserNotesChanged(val newValue: String) : Event
        object OnWrapUp : Event
    }
}