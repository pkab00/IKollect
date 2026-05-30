package com.vbshkn.ikollect.domain.model.candidate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
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
) : Parcelable {
    val displayName: String
        get() = "${artistCandidates.firstOrNull()?.name ?: "Unknown Artist"} - $name"
}