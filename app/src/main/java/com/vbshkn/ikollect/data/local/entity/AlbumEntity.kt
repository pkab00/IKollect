package com.vbshkn.ikollect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class AlbumEntity(
    @PrimaryKey(autoGenerate = true)
    val albumId: Long = 0,
    val masterId: Long,
    val barcodeNumber: String,
    val komcaNumber: String?,
    val name: String,
    val version: String?,
    val releaseDate: String?,
    val isFavorite: Boolean,
    val imageUrl: String?,
    val userNote: String?,
    val timestamp: Long = System.currentTimeMillis()
)
