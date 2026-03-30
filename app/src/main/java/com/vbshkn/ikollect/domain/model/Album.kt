package com.vbshkn.ikollect.domain.model

data class Album (
    val albumId: Long,
    val masterId: Long,
    val barcodeNumber: String,
    val name: String,
    val artists: List<Artist>,
    val version: String,
    val releaseDate: String,
    val isFavorite: Boolean,
    val coverImage: String?,
    val userNote: String,
    val savingTimestamp: Long
) {
    val extendedName = "${this.name} [${this.version}]"
}