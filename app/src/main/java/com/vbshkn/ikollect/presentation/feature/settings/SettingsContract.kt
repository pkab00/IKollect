package com.vbshkn.ikollect.presentation.feature.settings

sealed interface SettingsContract {
    sealed interface Effect {
        object GoBack : Effect
    }
    sealed interface Event {
        object OnBackClicked : Event
        object OnLogOutClicked : Event
        object OnChangeNicknameClicked : Event
        data object OnLogOutConfirmed : Event
        data object OnDismissDialog : Event
        data class OnNicknameFieldChanged(val newValue: String) : Event
        data class OnNewNicknameSelected(val newNickname: String) : Event
    }
}