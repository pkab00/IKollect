package com.vbshkn.ikollect.domain.model

data class AlbumOverview(
    val albumId: Long,
    val komcaNumber: String?,
    val name: String,
    val version: String,
    val isFavorite: Boolean,
    val imageUrl: String,
    val timestamp: Long
) {
    val extendedName = "${this.name} [${this.version}]"
}
