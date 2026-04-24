package com.vbshkn.ikollect.presentation.feature.auth

import com.vbshkn.ikollect.util.UiText

sealed interface AuthContract {
    sealed interface Effect {
        object GoToRegistration : Effect
        object GoToLogin : Effect
        object ExitAuthFlow : Effect
        data class ShowToast(val message: UiText) : Effect
    }

    sealed interface Event {
        data class OnEmailChanged(val email: String) : Event
        data class OnPasswordChanged(val password: String) : Event
        object OnAlreadyHaveAccountClicked : Event
        object OnDontHaveAccountClicked : Event
        object OnLoginClicked : Event
        object OnRegisterClicked : Event
    }
}