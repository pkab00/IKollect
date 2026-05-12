package com.vbshkn.ikollect.presentation.feature.userprofile

import com.vbshkn.ikollect.domain.error.AppError
import com.vbshkn.ikollect.domain.model.AppUser
import com.vbshkn.ikollect.domain.model.details.AlbumDetails
import com.vbshkn.ikollect.domain.model.list.AlbumListItem
import com.vbshkn.ikollect.domain.model.list.ArtistListItem
import com.vbshkn.ikollect.domain.model.list.PhotocardListItem

data class UserProfileUIState(
    val isLoading: Boolean = false,
    val isLoadingItems: Boolean = false,
    val error: AppError? = null,
    val user: AppUser? = null,
    val favoriteAlbums: List<AlbumDetails> = emptyList(),
    val favoritePhotocards: List<PhotocardListItem> = emptyList(),
    val favoriteArtists: List<ArtistListItem> = emptyList()
)
