package com.vbshkn.ikollect.data.remote.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GlobalArtistHierarchyBackend(
    @SerialName("group_id")
    val groupId: Long,
    @SerialName("member_id")
    val memberId: Long,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)