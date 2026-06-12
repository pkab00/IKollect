package com.vbshkn.ikollect.presentation.feature.settings

sealed interface SettingsContract {
    sealed interface Effect {
        object NavigateBack : Effect
        object NavigateToThemeSettings : Effect
        object NavigateToLanguageSettings : Effect
        object NavigateToTabsSettings : Effect
        object NavigateToTagSettings : Effect
    }
    sealed interface Event {
        object OnBackClicked : Event
        object OnLogOutClicked : Event
        object OnChangeNicknameClicked : Event
        object OnChangeThemeClicked : Event
        object OnChangeLanguageClicked : Event
        object OnConfigureTabsClicked : Event
        object OnManageTagsClicked : Event
        data object OnLogOutConfirmed : Event
        data object OnDismissDialog : Event
        data class OnNicknameFieldChanged(val newValue: String) : Event
        data class OnNewNicknameSelected(val newNickname: String) : Event
    }
}