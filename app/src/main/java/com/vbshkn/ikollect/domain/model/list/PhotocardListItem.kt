package com.vbshkn.ikollect.domain.model.list

import com.vbshkn.ikollect.domain.model.TagItem

data class PhotocardListItem(
    val photocardId: Long,
    val owner: ArtistListItem,
    val displayName: String,
    val tags: List<TagItem>,
    val imageUrl: String?
)