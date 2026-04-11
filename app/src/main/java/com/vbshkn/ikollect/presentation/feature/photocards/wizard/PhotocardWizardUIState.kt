package com.vbshkn.ikollect.presentation.feature.photocards.wizard

import com.vbshkn.ikollect.domain.model.list.AlbumListItem
import com.vbshkn.ikollect.domain.model.list.ArtistListItem
import com.vbshkn.ikollect.domain.model.candidate.PhotocardCandidate
import com.vbshkn.ikollect.domain.model.TagItem

data class PhotocardWizardUIState(
    val currentStep: Int = 0,
    val isLoading: Boolean = false,
    val photocardImagePreviews: List<String> = emptyList(),
    val enableTagSelector: Boolean = false,
    val dialogState: PhotocardWizardDialogState = PhotocardWizardDialogState.None,
    val photocardCandidate: PhotocardCandidate = PhotocardCandidate(),
    val artists: List<ArtistListItem> = emptyList(),
    val members: List<ArtistListItem> = emptyList(),
    val albums: List<AlbumListItem> = emptyList(),
    val tags: List<TagItem> = emptyList()
)
