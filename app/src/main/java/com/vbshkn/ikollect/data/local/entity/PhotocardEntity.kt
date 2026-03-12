package com.vbshkn.ikollect.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [
    ForeignKey(
        entity = AlbumEntity::class,
        parentColumns = ["komcaNumber"],
        childColumns = ["albumId"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.SET_NULL
    )
])
data class PhotocardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val albumId: Int,
    val displayName: String,
    val isPob: Boolean,
    val isFavorite: Boolean,
    val imageUrl: String?,
    val userNote: String?
)
