package com.vbshkn.ikollect.presentation.feature.settings.tabs

import com.vbshkn.ikollect.presentation.navigation.NavBarDestinations

sealed interface TabsSettingsContract {
    sealed interface Effect {
        data object NavigateBack : Effect
    }
    sealed interface Event {
        data object OnNavigateBackClicked : Event
        data class OnTabsReordered(val newOrder: List<NavBarDestinations>) : Event
    }
}