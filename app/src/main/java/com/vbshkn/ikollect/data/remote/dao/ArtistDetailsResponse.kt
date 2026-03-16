package com.vbshkn.ikollect.data.remote.dao

import com.google.gson.annotations.SerializedName

data class ArtistDetailsResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("namevariations")
    val nameVariations: List<String>,
    @SerializedName("images")
    val images: List<ImageDao>,
    @SerializedName("members")
    val members: List<ArtistDao>
)

data class ImageDao(
    @SerializedName("uri")
    val uri: String
)
