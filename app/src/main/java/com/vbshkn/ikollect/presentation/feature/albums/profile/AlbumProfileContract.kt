package com.vbshkn.ikollect.presentation.feature.albums.profile

sealed interface AlbumProfileContract {
    sealed interface Effect {
        object NavigateBack : Effect
        data class NavigateToArtist(val id: Long) : Effect
    }
    sealed interface Event {
        object OnBackClicked : Event
        object OnOwnerClicked : Event
        data class OnArtistCardClicked(val id: Long) : Event
    }
}