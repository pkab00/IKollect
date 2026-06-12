package com.vbshkn.ikollect.presentation.feature.settings.tabs

import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.presentation.feature.settings.tabs.TabsSettingsContract.Effect
import com.vbshkn.ikollect.presentation.feature.settings.tabs.TabsSettingsContract.Event
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.get.GetAppSettingsUseCase
import com.vbshkn.ikollect.domain.usecase.update.UpdateNavBarTabsUseCase
import com.vbshkn.ikollect.presentation.navigation.NavBarDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class TabsSettingsViewModel @Inject constructor(
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val updateNavBarTabsUseCase: UpdateNavBarTabsUseCase
) : BaseViewModel<TabsSettingsUiState, Event, Effect>(initialState = TabsSettingsUiState()) {
    init {
        viewModelScope.launch {
            getAppSettingsUseCase().collect { settings ->
                updateState { state -> state.copy(settings = settings) }
            }
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.OnNavigateBackClicked -> {
                sendEffect(Effect.NavigateBack)
            }
            is Event.OnTabsReordered -> viewModelScope.launch {
                updateNavBarTabsUseCase(event.newOrder)
            }
        }
    }

}