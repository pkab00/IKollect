package com.vbshkn.ikollect.data.local.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey

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
    val tagId: Long
)
