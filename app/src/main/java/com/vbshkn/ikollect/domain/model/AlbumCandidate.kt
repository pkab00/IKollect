package com.vbshkn.ikollect.domain.model

data class AlbumCandidate(
    val albumId: Int,
    val masterId: Int,
    val barcodeNumber: String,
    val name: String,
    val artists: List<Artist>,
    val versionOptions: List<String>,
    val releaseDate: String,
    val isFavorite: Boolean,
    val imageOptions: List<String>,
    val userNote: String
)
