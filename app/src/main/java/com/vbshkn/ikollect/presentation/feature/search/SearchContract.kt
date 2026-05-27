package com.vbshkn.ikollect.presentation.feature.search

sealed interface SearchContract {
    sealed interface Effect {
        data object NavigateBack : Effect
        data class NavigateToDetail(val id: Long) : Effect
    }
    sealed interface Event {
        data class OnQueryChange(val query: String) : Event
        data object OnClearQuery : Event
        data object OnNavigateBack : Event
        data class OnNavigateToDetail(val id: Long) : Event
    }
}