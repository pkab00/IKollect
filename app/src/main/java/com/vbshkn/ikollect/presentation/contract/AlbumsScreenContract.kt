package com.vbshkn.ikollect.presentation.contract

interface AlbumsScreenContract {
    sealed interface Effect {
        data class NavigateToAlbum(val id: Long) : Effect
    }
    sealed interface Event {
        data class OnAlbumClicked(val id: Long) : Event
        data object OnStartScanningClicked : Event
    }
}