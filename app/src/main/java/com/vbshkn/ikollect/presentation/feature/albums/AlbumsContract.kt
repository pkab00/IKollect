package com.vbshkn.ikollect.presentation.feature.albums

interface AlbumsContract {
    sealed interface Effect {
        data class NavigateToAlbum(val id: Long) : Effect
        data object NavigateToSaveFlow : Effect
    }
    sealed interface Event {
        data class OnAlbumClicked(val id: Long) : Event
        data object OnStartScanningClicked : Event
        data object OnDismissDialogClicked : Event
        data object OnAlbumSavingConfirmed : Event
    }
}