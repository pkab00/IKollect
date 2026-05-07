package com.vbshkn.ikollect.presentation.feature.albums.profile

sealed interface AlbumProfileContract {
    sealed interface Effect {
        object NavigateBack : Effect
        data class NavigateToArtist(val id: Long) : Effect
        data class NavigateToPhotocard(val id: Long) : Effect
        object NavigateToEdit : Effect
        object ShowRefreshingErrorToast : Effect

    }
    sealed interface Event {
        object OnBackClicked : Event
        object OnOwnerClicked : Event
        object OnEditClicked : Event
        data class OnArtistCardClicked(val id: Long) : Event
        data class OnPhotocardCardClicked(val id: Long) : Event
        object OnPulledToRefresh : Event
    }
}