package com.vbshkn.ikollect.data.local.model.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.vbshkn.ikollect.data.local.model.entity.PhotocardEntity

data class AlbumFullDetail(
    @Embedded
    val album: AlbumWithArtists,
    @Relation(
        entity = PhotocardEntity::class,
        parentColumn = "albumId",
        entityColumn = "photocardId",
    )
    val photocards: List<PhotocardMinimalDetail>
)
