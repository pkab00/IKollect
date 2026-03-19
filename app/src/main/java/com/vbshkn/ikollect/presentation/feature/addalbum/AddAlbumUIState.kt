package com.vbshkn.ikollect.presentation.feature.addalbum

import com.vbshkn.ikollect.domain.model.AlbumCandidate
import com.vbshkn.ikollect.domain.model.VersionCandidate
import com.vbshkn.ikollect.presentation.navigation.Route

data class AddAlbumUIState(
    val currentRoute: Route.AddAlbumFlow = Route.AddAlbumFlow.SeeInfo,
    val albumCandidate: AlbumCandidate? = null,
    val versionCandidate: VersionCandidate? = null,
    val dialogState: AddAlbumDialogState = AddAlbumDialogState.None
)
