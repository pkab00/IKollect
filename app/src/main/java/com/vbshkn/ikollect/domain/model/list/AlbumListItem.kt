package com.vbshkn.ikollect.domain.model.list

import com.vbshkn.ikollect.domain.model.Searchable

data class AlbumListItem(
    val albumId: Long,
    val komcaNumber: String?,
    val name: String,
    val version: String,
    val isFavorite: Boolean,
    val imageUrl: String,
    val timestamp: Long
) : Searchable {
    val extendedName = "${this.name} [${this.version}]"
    override fun matchesQuery(query: String): Boolean {
        return listOf(this.name, this.version, this.komcaNumber)
            .any {
                it?.contains(query, ignoreCase = true) == true
            }
    }
}