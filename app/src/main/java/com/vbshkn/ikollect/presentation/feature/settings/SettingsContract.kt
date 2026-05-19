package com.vbshkn.ikollect.presentation.feature.settings

import com.vbshkn.ikollect.data.local.datastore.LocalLanguage
import com.vbshkn.ikollect.data.local.datastore.LocalTheme
import com.vbshkn.ikollect.presentation.navigation.NavBarDestinations

sealed interface SettingsContract {
    sealed interface Effect {
        object GoBack : Effect
        object GoToThemeSettings : Effect
        object GoToLanguageSettings : Effect
        object GoToTabsSettings : Effect

    }
    sealed interface Event {
        object OnBackClicked : Event
        object OnLogOutClicked : Event
        object OnChangeNicknameClicked : Event
        object OnChangeThemeClicked : Event
        object OnChangeLanguageClicked : Event
        object OnConfigureTabsClicked : Event
        data object OnLogOutConfirmed : Event
        data object OnDismissDialog : Event
        data class OnNicknameFieldChanged(val newValue: String) : Event
        data class OnNewNicknameSelected(val newNickname: String) : Event
        data class OnNewThemeSelected(val newTheme: LocalTheme) : Event
        data class OnNewLanguageSelected(val newLanguage: LocalLanguage) : Event
        data class OnTabsReordered(val newOrder: List<NavBarDestinations>) : Event
    }
}