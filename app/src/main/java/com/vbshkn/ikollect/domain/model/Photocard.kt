package com.vbshkn.ikollect.domain.model

data class Photocard(
    val photocardId: Long,
    val albumId: Int?,
    val displayName: String,
    val artists: List<Artist>,
    val isPob: Boolean,
    val isFavorite: Boolean,
    val imageUrl: String?,
    val userNote: String?
)
