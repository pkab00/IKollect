package com.vbshkn.ikollect.presentation.feature.settings.language

import com.vbshkn.ikollect.data.local.datastore.LocalLanguage

sealed interface LanguageSettingsContract {
    sealed interface Effect {
        data object NavigateBack : Effect
    }
    sealed interface Event {
        data object OnNavigateBackClicked : Event
        data class OnNewLanguageSelected(val newLanguage: LocalLanguage) : Event
    }
}