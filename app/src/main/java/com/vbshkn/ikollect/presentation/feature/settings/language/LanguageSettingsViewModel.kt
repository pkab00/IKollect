package com.vbshkn.ikollect.presentation.feature.settings.language

import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.presentation.feature.settings.language.LanguageSettingsContract.Effect
import com.vbshkn.ikollect.presentation.feature.settings.language.LanguageSettingsContract.Event
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.get.GetAppSettingsUseCase
import com.vbshkn.ikollect.domain.usecase.update.UpdateAppLanguageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageSettingsViewModel @Inject constructor(
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val updateAppLanguageUseCase: UpdateAppLanguageUseCase
) : BaseViewModel<LanguageSettingsUiState, Event, Effect>(initialState = LanguageSettingsUiState()) {
    init {
        viewModelScope.launch {
            getAppSettingsUseCase()
                .distinctUntilChanged { old, new -> old.language == new.language }
                .collect { settings ->
                    updateState { it.copy(settings = settings) }
                }
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.OnNavigateBackClicked -> {
                sendEffect(Effect.NavigateBack)
            }
            is Event.OnNewLanguageSelected -> viewModelScope.launch {
                updateAppLanguageUseCase(event.newLanguage)
            }
        }
    }
}