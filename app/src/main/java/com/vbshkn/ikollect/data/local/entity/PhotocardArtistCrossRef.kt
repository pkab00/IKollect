package com.vbshkn.ikollect.data.local.entity

import androidx.room.Entity

@Entity(primaryKeys = ["photocardId", "artistId"])
data class PhotocardArtistCrossRef(
    val photocardId: Long,
    val artistId: Long
)
