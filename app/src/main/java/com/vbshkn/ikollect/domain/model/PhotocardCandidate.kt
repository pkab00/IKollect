package com.vbshkn.ikollect.domain.model

data class PhotocardCandidate(
    val imageUrl: String? = null,
    val ownerId: Long? = null,
    val isOwnerAGroup: Boolean = true,
    val albumId: Long? = null,
    val depictedArtistsId: List<Long> = emptyList(),
    val displayName: String = "",
    val isPob: Boolean = false,
    val userNote: String = ""
)
