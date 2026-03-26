package com.vbshkn.ikollect.data.remote.dao

import com.google.gson.annotations.SerializedName

data class ReleaseDetailsResponse(
    @SerializedName("title")
    val title: String,
    @SerializedName("artists")
    val artists: List<ArtistDao>,
    @SerializedName("styles")
    val styles: List<String>
)

data class ArtistDao(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String
)
