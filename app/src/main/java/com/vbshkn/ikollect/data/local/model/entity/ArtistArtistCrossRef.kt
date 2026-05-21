package com.vbshkn.ikollect.data.local.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import kotlin.time.Instant

@Entity(
    primaryKeys = ["groupId", "memberId"],
    foreignKeys = [
        ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["artistId"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["artistId"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE,
            deferred = true
        )
    ]
)
data class ArtistArtistCrossRef (
    val groupId: Long,
    val memberId: Long,
    val isSynchronized: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)