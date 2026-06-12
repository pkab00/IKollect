package com.vbshkn.ikollect.presentation.feature.settings.tag

import com.vbshkn.ikollect.domain.model.TagItem

sealed class TagSettingsDialogState {
    data class EditTagDialog(val selectedTag: TagItem) : TagSettingsDialogState()
    data object CreateTagDialog : TagSettingsDialogState()
    data object None : TagSettingsDialogState()
}