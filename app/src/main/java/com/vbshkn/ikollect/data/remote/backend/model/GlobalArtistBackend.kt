package com.vbshkn.ikollect.data.remote.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GlobalArtistBackend(
    @SerialName("artist_id")
    val artistId: Long,
    val name: String,
    @SerialName("is_group")
    val isGroup: Boolean = false,
    @SerialName("image_url")
    val imageUrl: String? = null
)