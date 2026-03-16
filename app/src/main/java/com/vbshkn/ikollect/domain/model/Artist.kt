package com.vbshkn.ikollect.domain.model

data class Artist(
    val artistId: Long,
    val name: String,
    val isGroup: Boolean,
    val members: List<Artist>?,
    val isFavorite: Boolean,
    val profileImage: String?
)
