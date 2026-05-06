package com.vbshkn.ikollect.data.remote.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotocardTagCrossRefBackend(
    @SerialName("photocard_id")
    val photocardId: Long,
    @SerialName("tag_id")
    val tagId: Long,
    @SerialName("creator_id")
    val creatorId: String? = null,
    @SerialName("owner_id")
    val ownerId: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("is_deleted")
    val isDeleted: Boolean = false
)