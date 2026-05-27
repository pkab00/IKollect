package com.vbshkn.ikollect.domain.model.details

import com.vbshkn.ikollect.domain.model.Searchable
import com.vbshkn.ikollect.domain.model.list.ArtistListItem

data class AlbumDetails (
    val albumId: Long,
    val masterId: Long,
    val barcodeNumber: String,
    val komcaNumber: String?,
    val name: String,
    val artists: List<ArtistListItem>,
    val version: String,
    val releaseDate: String,
    val isFavorite: Boolean,
    val coverImage: String?,
    val userNote: String,
    val savingTimestamp: Long
) : Searchable {
    val extendedName = "${this.name} [${this.version}]"
    override fun matchesQuery(query: String): Boolean {
        return (listOf(this.name, this.version, this.komcaNumber) + this.artists.map { it.name })
            .any {
                it?.contains(query, ignoreCase = true) == true
            }
    }
}