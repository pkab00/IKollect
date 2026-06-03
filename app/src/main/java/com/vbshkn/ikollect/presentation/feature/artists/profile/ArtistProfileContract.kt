package com.vbshkn.ikollect.presentation.feature.artists.profile

sealed interface ArtistProfileContract {
    sealed interface Effect {
        object NavigateBack : Effect
        data class NavigateToArtist(val id: Long) : Effect
        data class NavigateToAlbum(val id: Long) : Effect
        data class NavigateToPhotocard(val id: Long) : Effect
        object ShowRefreshingErrorToast : Effect

    }
    sealed interface Event {
        object OnBackClicked : Event
        data class OnArtistCardClicked(val id: Long) : Event
        data class OnAlbumCardClicked(val id: Long) : Event
        data class OnPhotocardCardClicked(val id: Long) : Event
        data class OnLikeClicked(val id: Long) : Event
        object OnPulledToRefresh : Event
    }
}