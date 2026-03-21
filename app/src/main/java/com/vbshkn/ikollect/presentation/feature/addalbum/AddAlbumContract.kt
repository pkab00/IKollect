package com.vbshkn.ikollect.presentation.feature.addalbum

import com.vbshkn.ikollect.domain.model.VersionCandidate

interface AddAlbumContract {
    sealed interface Effect {
        object NavigateBack : Effect
        object NavigateNext : Effect
        object Exit : Effect
        object OpenGallery : Effect
        object TryOpenCamera : Effect
    }
    sealed interface Event {
        object OnBackClicked : Event
        object OnNextClicked : Event
        object OnExitClicked : Event
        object OnExitConfirmed : Event
        object OnDismissDialog : Event
        object OnSelectPicture : Event
        object OnTakePicture : Event
        object OnShowCameraRationale : Event
        data class OnUpdateVersion(val candidate: VersionCandidate) : Event
        data class OnUpdateCover(val path: String) : Event
    }
}