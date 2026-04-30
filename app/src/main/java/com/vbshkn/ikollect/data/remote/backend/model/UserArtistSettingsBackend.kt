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
    val isFavorite: Boolean = false
)