package com.vbshkn.ikollect.data.local.model.entity

import androidx.room.Entity

@Entity(primaryKeys = ["photocardId", "artistId"])
data class PhotocardArtistCrossRef(
    val photocardId: Long,
    val artistId: Long
)
