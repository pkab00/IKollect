package com.vbshkn.ikollect.presentation.feature.settings.tag

import com.vbshkn.ikollect.domain.model.TagItem

data class TagSettingsUiState(
    val customTags: List<TagItem> = emptyList(),
    val systemTags: List<TagItem> = emptyList(),
    val dialogState: TagSettingsDialogState = TagSettingsDialogState.None
)
