package com.vbshkn.ikollect.data.remote.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserAlbumBackend(
    @SerialName("album_id")
    val albumId: Long? = null,
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("master_id")
    val masterId: Long? = null,
    @SerialName("barcode_number")
    val barcodeNumber: String? = null,
    @SerialName("komca_number")
    val komcaNumber: String? = null,
    val name: String,
    val version: String? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("is_favorite")
    val isFavorite: Boolean = false,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("user_note")
    val userNote: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null // ISO string
)