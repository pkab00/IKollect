package com.vbshkn.ikollect.domain.model

data class ArtistOverview(
    val artistId: Long,
    val name: String,
    val isGroup: Boolean,
    val isFavorite: Boolean,
    val imageUrl: String?,
    val albumsCount: Int,
    val photocardsCount: Int
)
