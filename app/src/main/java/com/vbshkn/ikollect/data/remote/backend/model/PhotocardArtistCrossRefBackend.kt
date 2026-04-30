package com.vbshkn.ikollect.data.remote.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotocardArtistCrossRefBackend(
    @SerialName("photocard_id")
    val photocardId: Long,
    @SerialName("artist_id")
    val artistId: Long,
    @SerialName("user_id")
    val userId: String? = null
)