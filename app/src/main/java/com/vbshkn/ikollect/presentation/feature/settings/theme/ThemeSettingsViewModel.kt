package com.vbshkn.ikollect.presentation.feature.settings.theme

import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.presentation.feature.settings.theme.ThemeSettingsContract.Event
import com.vbshkn.ikollect.presentation.feature.settings.theme.ThemeSettingsContract.Effect
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.get.GetAppSettingsUseCase
import com.vbshkn.ikollect.domain.usecase.update.UpdateAppThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeSettingsViewModel @Inject constructor(
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val updateAppThemeUseCase: UpdateAppThemeUseCase
) : BaseViewModel<ThemeSettingsUiState, Event, Effect>(initialState = ThemeSettingsUiState()) {
    init {
        viewModelScope.launch {
            getAppSettingsUseCase().collect { settings ->
                updateState { it.copy(settings = settings) }
            }
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.OnNavigateBackClicked -> {
                sendEffect(Effect.NavigateBack)
            }
            is Event.OnNewThemeSelected -> viewModelScope.launch {
                updateAppThemeUseCase(event.newTheme)
            }
        }
    }

}