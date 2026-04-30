package com.vbshkn.ikollect.data.remote.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GlobalArtistHierarchyBackend(
    @SerialName("group_id")
    val groupId: Long,
    @SerialName("member_id")
    val memberId: Long
)