package com.vbshkn.ikollect.presentation.feature.photocards.list

import com.vbshkn.ikollect.domain.error.AppError
import com.vbshkn.ikollect.domain.model.TagItem
import com.vbshkn.ikollect.domain.model.list.PhotocardListItem

data class PhotocardsUIState(
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val error: AppError? = null,
    val photocards: List<PhotocardListItem> = emptyList(),
    val tags: List<TagItem> = emptyList(),
    val selectedTag: TagItem? = null,
    val fullScreenPreview: String? = null
)
