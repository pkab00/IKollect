package com.vbshkn.ikollect.presentation.feature.userprofile

sealed interface UserProfileContract {
    sealed interface Effect {
        object GoToAuthScreen : Effect
    }
    sealed interface Event {
        object OnLogInClick : Event
        object OnLogOutClick : Event
    }
}