package com.vbshkn.ikollect.data.local.model.entity

import androidx.room.Entity

@Entity(primaryKeys = ["groupId", "memberId"])
data class ArtistArtistCrossRef (
    val groupId: Long,
    val memberId: Long
)