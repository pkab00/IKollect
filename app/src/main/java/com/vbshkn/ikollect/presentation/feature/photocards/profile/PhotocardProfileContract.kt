package com.vbshkn.ikollect.presentation.feature.photocards.profile

sealed interface PhotocardProfileContract {
    sealed interface Effect {
        object NavigateBack : Effect
        data class NavigateToArtist(val id: Long) : Effect
        data class NavigateToAlbum(val id: Long) : Effect
        data class NavigateToEdit(val id: Long) : Effect
        object ShowRefreshingErrorToast : Effect

    }
    sealed interface Event {
        object OnBackClicked : Event
        object OnEditClicked : Event
        object OnOwnerCardClicked : Event
        object OnAlbumCardClicked : Event
        data class OnArtistCardClicked(val id: Long) : Event
        data class OnLikeClicked(val id: Long, val isLiked: Boolean) : Event
        object OnPulledToRefresh : Event
        object OnDeletionConfirmed : Event
        object OnDeleteClicked : Event
        object OnDismissDialogClicked : Event
    }
}