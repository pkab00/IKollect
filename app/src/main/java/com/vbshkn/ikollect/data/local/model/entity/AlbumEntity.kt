package com.vbshkn.ikollect.data.local.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Instant

@Entity
data class AlbumEntity(
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
    val isSynchronized: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
