package com.vbshkn.ikollect.presentation.feature.artists.list

import com.vbshkn.ikollect.domain.business.ArtistFilter

sealed interface ArtistsContract {
    sealed interface Effect {
        data class NavigateToArtist(val artistId: Long) : Effect
        data object NavigateToSearch : Effect
        data object ShowRefreshingErrorToast : Effect
    }

    sealed interface Event {
        data object OnPulledToRefresh : Event
        data class OnArtistClick(val artistId: Long) : Event
        data class OnSelectFilter(val filter: ArtistFilter) : Event
        data object OnSearchClick : Event
    }
}