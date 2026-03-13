package com.vbshkn.ikollect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ArtistEntity(
    @PrimaryKey(autoGenerate = true)
    val artistId: Long,
    val name: String,
    val isGroup: Boolean,
    val parentGroupId: Long?,
    val isFavorite: Boolean,
    val imageUrl: String?
)