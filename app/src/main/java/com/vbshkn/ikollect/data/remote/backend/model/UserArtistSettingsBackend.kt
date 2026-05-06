package com.vbshkn.ikollect.data.remote.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserArtistSettingsBackend(
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("artist_id")
    val artistId: Long,
    @SerialName("is_favorite")
    val isFavorite: Boolean = false,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("is_deleted")
    val isDeleted: Boolean = false
)