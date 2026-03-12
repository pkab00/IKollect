package com.vbshkn.ikollect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class AlbumEntity(
    @PrimaryKey(autoGenerate = false)
    val komcaNumber: Int,
    val barcodeNumber: Int,
    val name: String,
    val version: String?,
    val releaseDate: String?,
    val isFavorite: Boolean,
    val imageUrl: String?,
    val userNote: String?
)
