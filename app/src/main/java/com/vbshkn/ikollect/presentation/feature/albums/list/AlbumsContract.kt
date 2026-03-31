package com.vbshkn.ikollect.presentation.feature.albums.list

import com.vbshkn.ikollect.domain.model.AlbumCandidate

interface AlbumsContract {
    sealed interface Effect {
        data class NavigateToAlbum(val id: Long) : Effect
        data class NavigateToSaveFlow(val candidate: AlbumCandidate) : Effect
    }
    sealed interface Event {
        data class OnAlbumClicked(val id: Long) : Event
        data object OnStartScanningClicked : Event
        data object OnDismissDialogClicked : Event
        data object OnAlbumSavingConfirmed : Event
    }
}