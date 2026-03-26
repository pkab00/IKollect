package com.vbshkn.ikollect.data.local.entity

import androidx.room.Entity

@Entity(primaryKeys = ["groupId", "memberId"])
data class ArtistArtistCrossRef (
    val groupId: Long,
    val memberId: Long
)