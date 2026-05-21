package com.vbshkn.ikollect.domain.model.candidate

import com.vbshkn.ikollect.domain.model.UserItemImage

data class PhotocardCandidate(
    val image: UserItemImage? = null,
    val ownerId: Long? = null,
    val isOwnerAGroup: Boolean = true,
    val albumId: Long? = null,
    val depictedArtistsId: List<Long> = emptyList(),
    val tagIds: Set<Long> = emptySet(),
    val displayName: String = "",
    val userNote: String = ""
)