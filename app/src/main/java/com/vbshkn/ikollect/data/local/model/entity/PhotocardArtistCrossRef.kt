package com.vbshkn.ikollect.data.local.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["photocardId", "artistId"],
    foreignKeys = [
        ForeignKey(
            entity = PhotocardEntity::class,
            parentColumns = ["photocardId"],
            childColumns = ["photocardId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["artistId"],
            childColumns = ["artistId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PhotocardArtistCrossRef(
    val photocardId: Long,
    val artistId: Long,
    val isSynchronized: Boolean = false
)
