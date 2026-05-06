package com.vbshkn.ikollect.data.remote.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPhotocardBackend(
    @SerialName("photocard_id")
    val photocardId: Long? = null,
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("album_id")
    val albumId: Long? = null,
    @SerialName("owner_id")
    val ownerId: Long,
    @SerialName("display_name")
    val displayName: String,
    @SerialName("is_favorite")
    val isFavorite: Boolean = false,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("user_note")
    val userNote: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("is_deleted")
    val isDeleted: Boolean = false
)