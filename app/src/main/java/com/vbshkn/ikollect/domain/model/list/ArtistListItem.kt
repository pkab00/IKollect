package com.vbshkn.ikollect.domain.model.list

import com.vbshkn.ikollect.domain.model.Searchable

data class ArtistListItem(
    val artistId: Long,
    val name: String,
    val isGroup: Boolean,
    val isFavorite: Boolean,
    val profileImage: String?
) : Searchable {
    override fun matchesQuery(query: String): Boolean {
        return name.contains(query, ignoreCase = true)
    }
}