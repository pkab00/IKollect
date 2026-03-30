package com.vbshkn.ikollect.data.local.model.pojo

data class ArtistMinimalDetail(
    val artistId: Long,
    val name: String,
    val isGroup: Boolean,
    val isFavorite: Boolean,
    val imageUrl: String?,
    val albumsCount: Int,
    val photocardsOwnedCount: Int,
    val photocardsDepictedCount: Int
)