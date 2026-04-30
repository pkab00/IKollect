package com.vbshkn.ikollect.data.remote.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TagBackend(
    @SerialName("tag_id")
    val tagId: Long? = null, // null для вставки
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("is_system_tag")
    val isSystemTag: Boolean = false,
    @SerialName("tag_name")
    val tagName: String,
    @SerialName("tag_color")
    val tagColor: Long
)