package com.vbshkn.ikollect.presentation.feature.photocards.profile

sealed interface PhotocardProfileContract {
    sealed interface Effect {
        object NavigateBack : Effect
        data class NavigateToArtist(val id: Long) : Effect
        data class NavigateToAlbum(val id: Long) : Effect
    }
    sealed interface Event {
        object OnBackClicked : Event
        object OnOwnerCardClicked : Event
        object OnAlbumCardClicked : Event
        data class OnArtistCardClicked(val id: Long) : Event
    }
}