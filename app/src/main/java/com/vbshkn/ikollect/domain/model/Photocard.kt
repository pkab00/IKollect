package com.vbshkn.ikollect.domain.model

data class Photocard(
    val photocardId: Long,
    val albumId: Int?,
    val ownerId: Long,
    val displayName: String,
    val depictedArtists: List<Artist>,
    val isPob: Boolean,
    val isFavorite: Boolean,
    val imageUrl: String?,
    val userNote: String?
)
