package com.vbshkn.ikollect.presentation.feature.photocards.profile.edit

import com.vbshkn.ikollect.domain.error.AppError
import com.vbshkn.ikollect.domain.model.TagItem
import com.vbshkn.ikollect.domain.model.profile.PhotocardProfileData

data class EditPhotocardProfileUIState(
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val enableTagSelector: Boolean = false,
    val dialogState: EditPhotocardProfileDialogState = EditPhotocardProfileDialogState.None,

    val image: String? = null,
    val oldImage: String? = null,
    val photocardName: String = "",
    val userNotes: String = "",
    val oldTagIds: Set<Long> = emptySet(),
    val selectedTagIds: Set<Long> = emptySet(),
    val allTags: List<TagItem> = emptyList(),
)

