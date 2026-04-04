package com.vbshkn.ikollect.presentation.feature.photocards.wizard

import com.vbshkn.ikollect.domain.model.AlbumOverview
import com.vbshkn.ikollect.domain.model.Artist
import com.vbshkn.ikollect.domain.model.ArtistOverview
import com.vbshkn.ikollect.domain.model.PhotocardCandidate

data class PhotocardWizardUIState(
    val currentStep: Int = 0,
    val isLoading: Boolean = false,
    val dialogState: PhotocardWizardDialogState = PhotocardWizardDialogState.None,
    val photocardCandidate: PhotocardCandidate = PhotocardCandidate(),
    val artistOverviews: List<ArtistOverview> = emptyList(),
    val members: List<Artist> = emptyList(),
    val albumOverviews: List<AlbumOverview> = emptyList()
)
