package com.vbshkn.ikollect.domain.model.list

data class PhotocardListItem(
    val photocardId: Long,
    val displayName: String,
    val imageUrl: String?,
    val isFavorite: Boolean
)