package com.vbshkn.ikollect.domain.model.details

import com.vbshkn.ikollect.domain.model.Searchable
import com.vbshkn.ikollect.domain.model.TagItem
import com.vbshkn.ikollect.domain.model.list.ArtistListItem

data class PhotocardDetails(
    val photocardId: Long,
    val owner: ArtistListItem,
    val displayName: String,
    val tags: List<TagItem>,
    val imageUrl: String?,
    val savingTimestamp: Long,
    val isFavorite: Boolean,
    val userNotes: String
) : Searchable {
    override fun matchesQuery(query: String): Boolean {
        return displayName.contains(query, ignoreCase = true)
    }
}
