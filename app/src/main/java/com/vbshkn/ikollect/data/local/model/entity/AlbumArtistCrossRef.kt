package com.vbshkn.ikollect.data.local.model.entity

import androidx.room.Entity

@Entity(primaryKeys = ["albumId", "artistId"])
data class AlbumArtistCrossRef(
    val albumId: Long,
    val artistId: Long
)
