package com.vbshkn.ikollect.domain.model

data class Album (
    val albumId: Int,
    val masterId: Int,
    val barcodeNumber: String,
    val name: String,
    val artists: List<Artist>,
    val version: String,
    val releaseDate: String,
    val isFavorite: Boolean,
    val coverImage: String?,
    val userNote: String
)