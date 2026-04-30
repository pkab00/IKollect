package com.vbshkn.ikollect.data.remote.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotocardTagCrossRefBackend(
    @SerialName("photocard_id")
    val photocardId: Long,
    @SerialName("tag_id")
    val tagId: Long,
    @SerialName("user_id")
    val userId: String? = null
)