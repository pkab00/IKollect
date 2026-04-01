package com.vbshkn.ikollect.presentation.feature.albums.wizard

import com.vbshkn.ikollect.domain.model.AlbumCandidate
import com.vbshkn.ikollect.domain.model.VersionCandidate
import com.vbshkn.ikollect.presentation.navigation.Route

data class AlbumWizardUIState(
    val stepIndex: Int = 0,
    val albumCandidate: AlbumCandidate,
    val versionCandidate: VersionCandidate? = null,
    val komcaNumber: String? = null,
    val dialogState: AlbumWizardDialogState = AlbumWizardDialogState.None,
    val isCoverCached: Boolean = false
)
