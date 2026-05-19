package com.vbshkn.ikollect.presentation.feature.settings

import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.presentation.feature.settings.SettingsContract.Event
import com.vbshkn.ikollect.presentation.feature.settings.SettingsContract.Effect
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.ClearLocalDataUseCase
import com.vbshkn.ikollect.domain.usecase.auth.LogOutUseCase
import com.vbshkn.ikollect.domain.usecase.get.GetAppSettingsUseCase
import com.vbshkn.ikollect.domain.usecase.get.GetUserProfileUseCase
import com.vbshkn.ikollect.domain.usecase.update.UpdateAppLanguageUseCase
import com.vbshkn.ikollect.domain.usecase.update.UpdateAppThemeUseCase
import com.vbshkn.ikollect.domain.usecase.update.UpdateNavBarTabsUseCase
import com.vbshkn.ikollect.domain.usecase.update.UpdateUserNicknameUseCase
import com.vbshkn.ikollect.domain.usecase.validate.ValidateNicknameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val clearLocalDataUseCase: ClearLocalDataUseCase,
    private val validateNicknameUseCase: ValidateNicknameUseCase,
    private val updateUserNicknameUseCase: UpdateUserNicknameUseCase,
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val updateAppThemeUseCase: UpdateAppThemeUseCase,
    private val updateAppLanguageUseCase: UpdateAppLanguageUseCase,
    private val updateNavBarTabsUseCase: UpdateNavBarTabsUseCase
) : BaseViewModel<SettingsUIState, Event, Effect>(initialState = SettingsUIState()) {
    init {
        observeUser()
        observeSettings()
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.OnBackClicked -> {
                sendEffect(Effect.GoBack)
            }
            is Event.OnChangeNicknameClicked -> {
                updateState { it.copy(dialogState = SettingsDialogState.NewNicknameDialog) }
            }
            is Event.OnLogOutClicked -> {
                updateState { it.copy(dialogState = SettingsDialogState.ConfirmLogOutDialog) }
            }
            is Event.OnDismissDialog -> {
                updateState { it.copy(dialogState = SettingsDialogState.None) }
            }
            is Event.OnLogOutConfirmed -> viewModelScope.launch {
                updateState { it.copy(isLoading = true) }
                logOutUseCase()
                clearLocalDataUseCase()
                updateState { it.copy(isLoading = false) }
                sendEffect(Effect.GoBack)
            }
            is Event.OnNicknameFieldChanged -> {
                val validationError = validateNicknameUseCase(event.newValue)
                updateState { it.copy(nicknameValidationError = validationError) }
            }
            is Event.OnNewNicknameSelected -> viewModelScope.launch {
                updateUserNicknameUseCase(event.newNickname)
            }
            is Event.OnChangeThemeClicked -> {
                sendEffect(Effect.GoToThemeSettings)
            }
            is Event.OnNewThemeSelected -> viewModelScope.launch {
                updateAppThemeUseCase(event.newTheme)
            }

            is Event.OnChangeLanguageClicked -> {
                sendEffect(Effect.GoToLanguageSettings)
            }
            is Event.OnNewLanguageSelected -> viewModelScope.launch {
                updateAppLanguageUseCase(event.newLanguage)
            }
            is Event.OnConfigureTabsClicked -> {
                sendEffect(Effect.GoToTabsSettings)
            }
            is Event.OnTabsReordered -> viewModelScope.launch {
                updateNavBarTabsUseCase(event.newOrder)
            }
        }
    }

    private fun observeUser() = collectFlowIntoState(
        flow = getUserProfileUseCase(),
        onLoading = { state -> state.copy(isLoading = true) },
        onError = { state, error -> state.copy(isLoading = false) },
        onSuccess = { state, data -> state.copy(user = data, isLoading = false) }
    )

    private fun observeSettings() = viewModelScope.launch {
        getAppSettingsUseCase().collect { settings ->
            updateState { it.copy(settings = settings) }
        }
    }
}