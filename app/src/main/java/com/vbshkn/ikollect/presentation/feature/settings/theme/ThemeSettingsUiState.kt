package com.vbshkn.ikollect.presentation.feature.settings.theme

import com.vbshkn.ikollect.data.local.datastore.LocalTheme
import com.vbshkn.ikollect.domain.model.AppSettings

data class ThemeSettingsUiState (
    val settings: AppSettings? = null,
    val themes: List<LocalTheme> = LocalTheme.entries
)