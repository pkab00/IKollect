package com.vbshkn.ikollect.presentation.feature.settings.theme

import com.vbshkn.ikollect.data.local.datastore.LocalTheme

interface ThemeSettingsContract {
    sealed interface Effect {
        data object NavigateBack : Effect
    }
    sealed interface Event {
        data object OnNavigateBackClicked : Event
        data class OnNewThemeSelected(val newTheme: LocalTheme) : Event
    }
}