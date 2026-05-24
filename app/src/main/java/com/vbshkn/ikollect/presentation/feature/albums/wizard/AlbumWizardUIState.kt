package com.vbshkn.ikollect.presentation.feature.albums.wizard

import com.vbshkn.ikollect.domain.business.ArtistFilter
import com.vbshkn.ikollect.domain.model.UserItemImage
import com.vbshkn.ikollect.domain.model.candidate.AlbumCandidate
import com.vbshkn.ikollect.domain.model.candidate.VersionCandidate
import com.vbshkn.ikollect.presentation.navigation.Route

data class AlbumWizardUIState(
    val stepIndex: Int = 0,
    val albumCandidate: AlbumCandidate? = null,
    val versionCandidate: VersionCandidate? = null,
    val komcaNumber: String? = null,
    val dialogState: AlbumWizardDialogState = AlbumWizardDialogState.None,
    val coverImage: UserItemImage? = null,
    val albumCoverPreviews: List<UserItemImage> = emptyList()
)
