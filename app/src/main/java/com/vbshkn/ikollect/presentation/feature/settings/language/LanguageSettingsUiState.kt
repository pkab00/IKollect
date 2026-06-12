package com.vbshkn.ikollect.presentation.feature.settings.language

import com.vbshkn.ikollect.data.local.datastore.LocalLanguage
import com.vbshkn.ikollect.domain.model.AppSettings

data class LanguageSettingsUiState(
    val settings: AppSettings? = null,
    val languages: List<LocalLanguage> = LocalLanguage.entries
)
