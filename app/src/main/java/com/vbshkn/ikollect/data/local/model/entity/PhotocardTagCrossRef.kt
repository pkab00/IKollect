package com.vbshkn.ikollect.data.local.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import kotlin.time.Instant

@Entity(
    primaryKeys = ["photocardId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = PhotocardEntity::class,
            parentColumns = ["photocardId"],
            childColumns = ["photocardId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["tagId"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PhotocardTagCrossRef(
    val photocardId: Long,
    val tagId: Long,
    val isSynchronized: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
