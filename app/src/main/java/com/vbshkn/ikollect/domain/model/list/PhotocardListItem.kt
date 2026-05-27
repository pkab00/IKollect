package com.vbshkn.ikollect.domain.model.list

import com.vbshkn.ikollect.domain.model.Searchable

data class PhotocardListItem (
    val photocardId: Long,
    val displayName: String,
    val imageUrl: String?,
    val isFavorite: Boolean
) : Searchable {
    override fun matchesQuery(query: String): Boolean {
        return displayName.contains(query, ignoreCase = true)
    }
}