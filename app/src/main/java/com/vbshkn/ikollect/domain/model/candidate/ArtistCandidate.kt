package com.vbshkn.ikollect.domain.model.candidate

import kotlinx.serialization.Serializable

@Serializable
data class ArtistCandidate(
    val artistId: Long,
    val name: String,
    val isGroup: Boolean,
    val isFavorite: Boolean,
    val profileImage: String?,
    val memberIds: List<Long>
)