package com.vbshkn.ikollect.presentation.feature.artists.list

sealed interface ArtistsContract {
    sealed interface Effect {
        object ShowRefreshingErrorToast : Effect
    }

    sealed interface Event {
        object OnPulledToRefresh : Event
    }
}