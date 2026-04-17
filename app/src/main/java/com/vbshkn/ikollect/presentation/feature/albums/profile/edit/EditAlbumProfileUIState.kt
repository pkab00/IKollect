package com.vbshkn.ikollect.presentation.feature.albums.profile.edit

import com.vbshkn.ikollect.data.AppError
import com.vbshkn.ikollect.domain.model.profile.AlbumProfileData

data class EditAlbumProfileUIState(
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val dialogState: EditAlbumProfileDialogState = EditAlbumProfileDialogState.None,

    val image: String? = null,
    val oldImage: String? = null,
    val albumName: String = "",
    val albumVersion: String = "",
    val komcaNumber: String = "",
    val userNotes: String = ""
)
