package com.vbshkn.ikollect.presentation.feature.albums.list

import com.vbshkn.ikollect.domain.model.candidate.AlbumCandidate

interface AlbumsContract {
    sealed interface Effect {
        data class NavigateToAlbum(val id: Long) : Effect
        data object NavigateToSearch : Effect
        data class NavigateToSaveFlow(val candidate: AlbumCandidate) : Effect
        object ShowRefreshingErrorToast : Effect
    }
    sealed interface Event {
        data class OnAlbumClicked(val id: Long) : Event
        data object OnStartScanningClicked : Event
        data object OnSearchClicked : Event
        data object OnDismissDialogClicked : Event
        data object OnAlbumSavingConfirmed : Event
        data object OnPulledToSync : Event
    }
}