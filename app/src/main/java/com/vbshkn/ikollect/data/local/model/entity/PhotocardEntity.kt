package com.vbshkn.ikollect.data.local.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlin.time.Instant

@Entity(foreignKeys = [
    ForeignKey(
        entity = AlbumEntity::class,
        parentColumns = ["albumId"],
        childColumns = ["albumId"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.SET_NULL
    ),
    ForeignKey(
        entity = ArtistEntity::class,
        parentColumns = ["artistId"],
        childColumns = ["ownerId"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )
])
data class PhotocardEntity(
    @PrimaryKey(autoGenerate = true)
    val photocardId: Long = 0,
    val albumId: Long?,
    val ownerId: Long,
    val displayName: String,
    val isFavorite: Boolean,
    val imageUrl: String?,
    val userNote: String?,
    val savingTimestamp: Long = System.currentTimeMillis(),
    val isSynchronized: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
