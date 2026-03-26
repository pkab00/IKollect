package com.vbshkn.ikollect.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AlbumCandidate(
    val discogsAlbumId: Long,
    val masterId: Long,
    val barcodeNumber: String,
    val name: String,
    val artistCandidates: List<ArtistCandidate>,
    val versionCandidates: List<VersionCandidate>,
    val releaseDate: String,
    val isFavorite: Boolean,
    val userNote: String
)
