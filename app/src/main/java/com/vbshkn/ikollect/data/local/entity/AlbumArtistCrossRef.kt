package com.vbshkn.ikollect.data.local.entity

import androidx.room.Entity

@Entity(primaryKeys = ["albumId", "artistId"])
data class AlbumArtistCrossRef(
    val albumId: Long,
    val artistId: Long
)
