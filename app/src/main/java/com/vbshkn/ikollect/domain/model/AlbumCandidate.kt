package com.vbshkn.ikollect.domain.model

data class AlbumCandidate(
    val albumId: Int,
    val masterId: Int,
    val barcodeNumber: String,
    val name: String,
    val artists: List<Artist>,
    val versionCandidates: List<VersionCandidate>,
    val releaseDate: String,
    val isFavorite: Boolean,
    val userNote: String
)
