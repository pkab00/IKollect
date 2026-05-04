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
    val ownerId: String? = null
)