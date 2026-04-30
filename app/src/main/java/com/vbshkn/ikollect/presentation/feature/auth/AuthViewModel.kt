package com.vbshkn.ikollect.presentation.feature.auth

import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.data.SyncManager
import com.vbshkn.ikollect.domain.UserAuthError
import com.vbshkn.ikollect.presentation.feature.auth.AuthContract.Effect
import com.vbshkn.ikollect.presentation.feature.auth.AuthContract.Event
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.LogInUseCase
import com.vbshkn.ikollect.domain.usecase.LogOutUseCase
import com.vbshkn.ikollect.domain.usecase.RegisterUserUseCase
import com.vbshkn.ikollect.domain.usecase.ValidateEmailUseCase
import com.vbshkn.ikollect.domain.usecase.ValidateNicknameUseCase
import com.vbshkn.ikollect.domain.usecase.ValidatePasswordUseCase
import com.vbshkn.ikollect.presentation.auth.GoogleAuthUIClient
import com.vbshkn.ikollect.presentation.feature.auth.AuthContract.Effect.*
import com.vbshkn.ikollect.util.UiText.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val validateNicknameUseCase: ValidateNicknameUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val logInUseCase: LogInUseCase,
    private val logOutUseCase: LogOutUseCase,
    val googleAuthUIClient: GoogleAuthUIClient,
    private val syncManager: SyncManager
) : BaseViewModel<AuthUIState, Event, Effect>(initialState = AuthUIState()) {
    init {
        validateInput()
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.OnNicknameChanged -> {
                updateState { it.copy(nickname = event.nickname) }
            }
            is Event.OnEmailChanged -> {
                updateState { it.copy(email = event.email) }
            }
            is Event.OnPasswordChanged -> {
                updateState { it.copy(password = event.password) }
            }
            is Event.OnAlreadyHaveAccountClicked -> {
                sendEffect(GoToLogin)
            }
            is Event.OnDontHaveAccountClicked -> {
                sendEffect(GoToRegistration)
            }
            is Event.OnLoginClicked -> viewModelScope.launch {
                updateState { it.copy(isLoading = true) }
                val loginError = logInUseCase(uiState.value.email, uiState.value.password)
                if (loginError == null) {
                    syncManager.performInitialSync().join()
                    sendEffect(ExitAuthFlow)
                } else {
                    when (loginError) {
                        is UserAuthError.Login.InvalidUser -> {
                            sendEffect(ShowToast(StringResource(R.string.error_user_not_found)))
                        }
                        is UserAuthError.Login.UnknownError -> {
                            sendEffect(
                                ShowToast(
                                    StringResource(R.string.unknown_error_prefix) + DynamicString(loginError.message)
                                )
                            )
                        }
                    }
                }
                updateState { it.copy(isLoading = false) }
            }
            is Event.OnRegisterClicked -> viewModelScope.launch {
                updateState { it.copy(isLoading = true) }
                val registrationError = registerUserUseCase(
                    uiState.value.email, uiState.value.password, uiState.value.nickname
                )
                 if (registrationError == null) {
                     logOutUseCase()
                     sendEffect(GoToLogin)
                 } else {
                     when (registrationError) {
                         is UserAuthError.Registration.EmailAlreadyInUse -> {
                             sendEffect(ShowToast(StringResource(R.string.error_email_in_use)))
                             updateState { it.copy(email = "") }
                         }
                         is UserAuthError.Registration.UnknownError -> {
                             sendEffect(
                                 ShowToast(
                                     StringResource(R.string.unknown_error_prefix) + DynamicString(registrationError.message)
                                 )
                             )
                         }
                         else -> {}
                     }
                 }
                updateState { it.copy(isLoading = false) }
            }
            is Event.OnSignInWithGoogleClick -> {
                sendEffect(StartGoogleSignIn)
            }
            is Event.OnSignInWithGoogleSucceed -> viewModelScope.launch {
                updateState { it.copy(isLoading = true) }
                syncManager.performInitialSync().join()
                updateState { it.copy(isLoading = false) }
                sendEffect(ExitAuthFlow)
            }
        }
    }

    private fun validateInput() {
        viewModelScope.launch {
            uiState.collect { state ->
                val validationError = validateNicknameUseCase(state.nickname)
                if (validationError != state.nicknameValidationError) {
                    updateState { it.copy(nicknameValidationError = validationError) }
                }
            }
        }
        viewModelScope.launch {
            uiState.collect { state ->
                val validationError = validateEmailUseCase(state.email)
                if (validationError != state.emailValidationError) {
                    updateState { it.copy(emailValidationError = validationError) }
                }
            }
        }
        viewModelScope.launch {
            uiState.collect { state ->
                val validationError = validatePasswordUseCase(state.password)
                if (validationError != state.passwordValidationError) {
                    updateState { it.copy(passwordValidationError = validationError) }
                }
            }
        }
    }
}