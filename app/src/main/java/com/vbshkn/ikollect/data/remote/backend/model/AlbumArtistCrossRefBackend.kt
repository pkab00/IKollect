package com.vbshkn.ikollect.data.remote.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlbumArtistCrossRefBackend(
    @SerialName("album_id")
    val albumId: Long,
    @SerialName("artist_id")
    val artistId: Long,
    @SerialName("user_id")
    val userId: String? = null
)