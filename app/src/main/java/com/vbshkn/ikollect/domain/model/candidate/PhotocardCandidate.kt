package com.vbshkn.ikollect.domain.model.candidate

data class PhotocardCandidate(
    val imageUrl: String? = null,
    val ownerId: Long? = null,
    val isOwnerAGroup: Boolean = true,
    val albumId: Long? = null,
    val depictedArtistsId: List<Long> = emptyList(),
    val tagIds: Set<Long> = emptySet(),
    val displayName: String = "",
    val userNote: String = ""
)