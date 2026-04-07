package com.vbshkn.ikollect.domain.model.list

data class ArtistListItem(
    val artistId: Long,
    val name: String,
    val isGroup: Boolean,
    val isFavorite: Boolean,
    val profileImage: String?
)