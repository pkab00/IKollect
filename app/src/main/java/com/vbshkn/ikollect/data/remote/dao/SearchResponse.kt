package com.vbshkn.ikollect.data.remote.dao

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("results")
    val results: List<SearchResult>
)

data class SearchResult(
    @SerializedName("id")
    val id: Int,
    @SerializedName("master_id")
    val masterId: Int,
    @SerializedName("title")
    val title: String, // Artist - Release
    @SerializedName("year")
    val year: String,
    @SerializedName("cover_image")
    val coverImage: String,
    @SerializedName("formats")
    val formats: List<FormatDao>?
)

data class FormatDao(
    @SerializedName("text")
    val text: String?, // e.g. I Ver.
    @SerializedName("name")
    val name: String? // e.g. CD
)