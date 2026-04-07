package com.vbshkn.ikollect.domain.model.details

import com.vbshkn.ikollect.domain.model.list.ArtistListItem

data class AlbumDetails (
    val albumId: Long,
    val masterId: Long,
    val barcodeNumber: String,
    val name: String,
    val artists: List<ArtistListItem>,
    val version: String,
    val releaseDate: String,
    val isFavorite: Boolean,
    val coverImage: String?,
    val userNote: String,
    val savingTimestamp: Long
) {
    val extendedName = "${this.name} [${this.version}]"
}