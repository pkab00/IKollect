package com.vbshkn.ikollect.domain.model.list

data class AlbumListItem(
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